#Wow Web Framework
##起因
自己想写一个web框架
##项目说明
整个项目结构为:
wwf
    wwf-core(wwf的核心包)
    wwf-web(wwf与spring的集成演示)

##约定
1.整个项目应为标准的maven-archetype-webapp

2.在src目录中新建 java 目录

3.在resources目录中新建wwfconfig目录,目录中新建一个config.properties

4.在webapp目录下新建resources目录,用来存放静态资源

5.在webapp目录下新建views目录,用来存放视图

6.在java目录中新建自己的包,以***com.chitucode.demo***为例

 - 6.1 在config.properties中,输入wwf.basepackage=com.chitucode.demo
 - 6.2 在com.chitucode.demo包下新建controllers包,里面存放的是所有的controller,controller命名须以"Controller"结尾并继承自WWFController
 - 6.3 在com.chitucode.demo包下新建interceptors包,里面存放的是所有的interceptors(拦截器),interceptor命名须以"Interceptor"结尾并实现WWFinterceptor接口
 - 6.4 在com.chitucode.demo包下新建inits包,里面存放的是所有的初始化类,该包中的类须以Init结尾并且实现Init接口,类中的内容会在WWF的所有组件初始化前执行,此时可以做一些外部集成工作

以上是搭建框架的过程,具体请参考实例工程