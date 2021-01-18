# 使用美颜配置依赖的方式引用

* 1. 首先需要将 aar 文件放入引用Module的libs目录下，和一般的jar文件类似；
* 2. 然后在gradle配置文件中把libs 目录放入依赖：
> [Android美颜faceunity.aar下载地址](libs/faceunity.aar)
``` 
repositories{
        flatDir{
            dirs 'libs'
            }
        }
```java

* 3. 在gradle文件中使用依赖的方式引用aar，这一句依赖即可关联完毕：
     implementation(name:'faceunity',ext:'aar')
* 4. 重新构建一下工程，在Module的 build/intermediates/exploded-aar 目录下，既可以看到导入的aar生成的临时文件。