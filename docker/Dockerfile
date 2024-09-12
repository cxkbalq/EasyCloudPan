# 基础镜像
FROM openjdk:8

# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建目录
USER root
RUN mkdir -p /file/file
RUN mkdir -p /file/temp
RUN mkdir -p /file/lujing
# 更换为阿里云的镜像源
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list \
    && apt-get update \
    && apt-get install -y \
       procps \
       unzip \
       curl \
       bash \
       tzdata \
       yasm \
       ffmpeg \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 拷贝 jar 包
COPY EasyCloudPan-0.0.1-SNAPSHOT.jar /app.jar

# 入口
ENTRYPOINT ["java", "-jar", "/app.jar"]