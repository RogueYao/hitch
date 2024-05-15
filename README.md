
## 一键部署与启动

## 1、把代码上传到一台服务器

确保服务器安装了maven 、docker 、docker-compose

修改deploy下的docker-compose.yml里的配置，主要涉及以下几个：（其他不必动）

![image-20240510191150028](README.assets/image-20240510191150028.png)



## 2、到  allinone 下执行

mvn package docker:build



## 3、到deploy下执行

docker-compose up -d

*备注：docker-compose.yml默认启动allinone集成了静态文件的版本，如果需要动静分离，请启动带-nginx后缀的版本*



## 启动后的一些配置：

![image-20240510190608861](README.assets/image-20240510190608861.png)



各服务端口号：

![image-20240510190843129](README.assets/image-20240510190843129.png)