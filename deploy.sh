#!/bin/bash
set -e

IMAGE_TAG=$1

if [ -z "$IMAGE_TAG" ]; then
    echo "Usage: ./deploy.sh <IMAGE_TAG>"
    exit 1
fi

echo "Deploying image tag: $IMAGE_TAG"

# ----------------------------
# Environment variables
# ----------------------------
AWS_REGION="us-east-1"
ACCOUNT_ID="110425445190"
REPOSITORY="training-portal"
SECRET_NAME="username"
CONTAINER_NAME="training-portal"

# ----------------------------
# Log in to Amazon ECR
# ----------------------------
echo "Logging into Amazon ECR..."
aws ecr get-login-password \
    --region "$AWS_REGION" | docker login \
    --username AWS \
    --password-stdin "${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

# ----------------------------
# Fetch DB credentials from Secrets Manager
# ----------------------------
SECRET=$(aws secretsmanager get-secret-value \
    --secret-id "$SECRET_NAME" \
    --query SecretString \
    --output text)

SPRING_DATASOURCE_USERNAME=$(echo "$SECRET" | jq -r '.username')
SPRING_DATASOURCE_PASSWORD=$(echo "$SECRET" | jq -r '.password')
HOST=$(echo "$SECRET" | jq -r '.host')
PORT=$(echo "$SECRET" | jq -r '.port')
DBNAME=$(echo "$SECRET" | jq -r '.dbname')

SPRING_DATASOURCE_URL="jdbc:mysql://${HOST}:${PORT}/${DBNAME}"

# ----------------------------
# Pull the required image
# ----------------------------
IMAGE_URI="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPOSITORY}:${IMAGE_TAG}"
echo "Pulling image..."
docker pull "$IMAGE_URI"

# ----------------------------
# Stop the existing container
# ----------------------------
echo "Stopping existing container..."
docker stop "$CONTAINER_NAME" || true
docker rm "$CONTAINER_NAME" || true

# ----------------------------
# Start the new container
# ----------------------------
echo "Starting new container..."
docker run -d \
    --name "$CONTAINER_NAME" \
    -p 8080:8080 \
    -e SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
    -e SPRING_DATASOURCE_USERNAME="$SPRING_DATASOURCE_USERNAME" \
    -e SPRING_DATASOURCE_PASSWORD="$SPRING_DATASOURCE_PASSWORD" \
    "$IMAGE_URI"

echo ""
echo "========================================="
echo "Deployment completed successfully."
echo "Image: $IMAGE_URI"
echo "Container: $CONTAINER_NAME"
echo "========================================="
