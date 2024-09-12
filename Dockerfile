# 基础镜像
FROM openjdk:8

# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建目录
RUN mkdir /imge

# 安装必要的工具和 ffmpeg
RUN apt-get update && apt-get install -y \
    procps \
    unzip \
    curl \
    bash \
    tzdata \
    yasm \
    ffmpeg \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# 拷贝 jar 包
COPY reggie_waimai-0.0.1-SNAPSHOT.jar /app.jar

# 入口
ENTRYPOINT ["java", "-jar", "/app.jar"]