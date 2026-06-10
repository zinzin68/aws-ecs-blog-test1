# ecr ecs 실습

# awscli 설치
# mac - https://awscli.amazonaws.com/AWSCLIV2.pkg
# win - https://awscli.amazonaws.com/AWSCLIV2.msi

# mac은 brew를 통해 설치도 가능!
brew install awscli
aws --version

# C:\Users\(사용자)\.aws
# 인증 수행하기
# 1. access key 등록 - 본인키
# 2. secret key 등록 - 본인 비밀키
# 3. region 등록 - ap-northeast-2
# 4. format 등록 - json
aws configure

# 정상 설정 확인
aws sts get-caller-identity

# 계정 ID
# 587129418959

# ECR 인증 (AWS CLI v2 기준)
aws ecr get-login-password --region ap-northeast-2 \
 | docker login --username AWS --password-stdin \
 {AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com

aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 587129418959.dkr.ecr.ap-northeast-2.amazonaws.com

# ECR 리포지토리 생성(있으면 생성 안해도 됨)
aws ecr create-repository --region ap-northeast-2 --repository-name dev/my-repo

# dockerfile 빌드
docker build -t my-blog-app:1.0.0 .

# 로컬에서 빌드한 이미지에 ECR 태그 지정
docker tag {appname:tag} \
 {AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/{repo}/{appname:tag}

docker tag my-blog-app:1.0.0 587129418959.dkr.ecr.ap-northeast-2.amazonaws.com/dev/my-repo:my-blog-app-1.0.0

# 이미지를 ECR로 푸시
docker push {AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/{repo}/{appname}
docker push 587129418959.dkr.ecr.ap-northeast-2.amazonaws.com/dev/my-repo:my-blog-app-1.0.0

# ECR 리포지토리의 이미지 목록 확인
aws ecr describe-images --repository-name dev/my-repo --region ap-northeast-2


# ECR 업데이트 끝!

# Parameter Store 등록 (.env 파일 기반)
# .env 파일이 있는 프로젝트 루트에서 실행

# Mac / Git Bash
grep -v '^\s*#' .env | grep -v '^\s*$' | while IFS='=' read -r k v; do aws ssm put-parameter --name "/myapp/$k" --value "$v" --type "SecureString" --overwrite; done

# Windows PowerShell
Get-Content .env | Where-Object { $_ -notmatch '^\s*#' -and $_ -notmatch '^\s*$' } | ForEach-Object { $p = $_ -split '=',2; aws ssm put-parameter --name "/myapp/$($p[0].Trim())" --value "$($p[1].Trim())" --type "SecureString" --overwrite }

# 등록된 파라미터 목록 확인
aws ssm describe-parameters --region ap-northeast-2

