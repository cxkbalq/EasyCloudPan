# EasyCloudPan



### 项目简介：

​      这个项目是一个防百度云盘的前后端分离的项目，支持文件上传（秒传，分片），下载，分享，保存文件，视频，音乐播放，文档浏览等功能，支持对违规文件的封禁，用户封禁，符合云盘的基本特征，本项目还采取了redis用于缓存，提高并发量，对数据一致性进行了特殊处理，这个项目是b站up主（easypan）另一个版本，我采用了给up主的前端，并进行了二次开发，以及一些bug的修复，部分逻辑与接口和原版本并不配，添加了部分功能，具体请看我仓库：https://github.com/cxkbalq/EasyPan-Portal.git，**本项目完全自主开发（所有逻辑均由自己构思），采用mybatis-plus，与原项目所采取的工具不同，以及对功能实现的方法和代码逻辑完全不同。对此，还添加了docker（在最下面）的自动部署**



### 技术栈：

###### springboot    vue    mybatis-plus   redis   jwt  docker  ffmpeg



### 应用截图：



**主页：**

![1](https://github.com/cxkbalq/EasyCloudPan/blob/master/image/1.png)



**播放：**![2](https://github.com/cxkbalq/EasyCloudPan/blob/master/image/2.png)



**分享：**

![3](https://github.com/cxkbalq/EasyCloudPan/blob/master/image/3.png)



**回收站：![2](https://github.com/cxkbalq/EasyCloudPan/blob/master/image/4.png)**

**管理员：**![2](https://github.com/cxkbalq/EasyCloudPan/blob/master/image/5.png)



**更加美观的邮箱发送：**![2](https://github.com/cxkbalq/EasyCloudPan/blob/master/image/6.png)





具体更多功能请自行部署！





### 后续更新想法：

1.添加自定义文件路径保存文件

2.支持文件定期双重备份以及spl的备份（适用于家庭的场景，数据无价）

3.支持邮箱自定义模板（自己换用自己喜爱的邮箱模板）

4.支持七牛云等免费oss，保存重要数据，（让你无需高带宽的公网或内网穿透服务，即可远程访问家里的内容或提高访问速度）

5.完成对手机端的支持（已完成）



当然由于时间关系慢慢完善，本人也只是学生时间有限，而且由于技术的不足，并且未经过大规模的使用，可能还是有很多**bug，请联系我：1951196623@qq.com**,**我会进行修改，感谢你的提出**



## docker部署



##### 1.基本条件：



请你帮保证下面所有文件在同一目录下（项目里全部提供了docker文件里）

/file 挂载图片数据（直接将我项目下的/imge导入即可，目录下以存在测试图片），

/EasyCloudPan-0.0.1-SNAPSHOT.jar

/Dockerfile

/docker-compose.yml

/init.sql 初始化数据库

docker环境自行安装

如果修改进行端口修改，请自行修改docker-compose.yml文件

nginx对外端口80：88

mysql对外端口：3308：3306

java_jar对外端口 : 8082: 8080

redis对外端口：6380：6379



在拉取镜像时可能由于镜像源不同，有的版本找不到，所以我将java的具体镜像改为了8，可以会找不到，自己找的8的jdk拉取一下就好了

##### 2.执行



拉取项目

```
git clone https://github.com/cxkbalq/EasyCloudPan.git
```



进入到docker目录（我已经提供了全部文件，理论上不会缺少文件，直接进入即可)

```
cd reggie-waimai/docker
```



启动项目

```
sudo docker compose up -d
```



查看是否运行

```
docker ps -a
```



欧克，完成！！！！
