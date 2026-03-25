# AWS Deployment Architecture

## Infrastructure Overview
```
Internet
    ↓
Application Load Balancer (ALB)
    ↓
AWS EKS Cluster (Kubernetes)
    ├── portfolio-service (3 pods)
    ├── Redis pod
    └── PostgreSQL pod → AWS RDS (production)
```

## AWS Services Used

| Service | Purpose |
|---------|---------|
| EC2 | Worker nodes for EKS cluster |
| EKS | Managed Kubernetes service |
| RDS | Managed PostgreSQL database |
| ElastiCache | Managed Redis cache |
| ALB | Load balancer for traffic distribution |
| ECR | Docker image registry |
| IAM | Security roles and policies |
| CloudWatch | Logs and monitoring |

## Setup Commands
```bash
# 1. Create EKS cluster
eksctl create cluster \
  --name fidelity-cluster \
  --region us-east-1 \
  --nodes 3 \
  --node-type t3.medium

# 2. Connect kubectl to cluster
aws eks update-kubeconfig --name fidelity-cluster

# 3. Deploy secrets first
kubectl apply -f k8s/secrets.yml

# 4. Deploy database and cache
kubectl apply -f k8s/postgres.yml
kubectl apply -f k8s/redis.yml

# 5. Deploy application
kubectl apply -f k8s/deployment.yml
kubectl apply -f k8s/service.yml

# 6. Check deployment status
kubectl get pods
kubectl get services

# 7. Get the Load Balancer URL
kubectl get service portfolio-service
```

## Scaling Commands
```bash
# Scale up to 5 instances during market hours
kubectl scale deployment portfolio-service --replicas=5

# Scale back down after hours
kubectl scale deployment portfolio-service --replicas=2

# Auto-scaling based on CPU
kubectl autoscale deployment portfolio-service \
  --min=2 --max=10 --cpu-percent=70
```

## Why These Choices

- **EKS**: Managed Kubernetes — AWS handles control plane
- **RDS**: Managed PostgreSQL — automated backups, failover
- **ElastiCache**: Managed Redis — no manual setup needed
- **ALB**: Distributes traffic across all 3 pods automatically
- **3 replicas**: High availability — one pod failure = others handle traffic