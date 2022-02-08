1.基于Mvvm模式集成谷歌官方推荐的JetPack组件库LiveData+ViewModel+DataBinding，以ARouter为组件路由实现的玩Android开放API安卓客户端
2.项目结合OkHttp+Rxjava2+Gson+Retrofit组合实现网络请求、Glide图像请求、ARoute路由框架、腾讯MMKV替代Sharedpreferences实现高性能本地缓存、
基于LiveData的消息总线LiveEventbus事件分发等等
3.一个通用组件加多业务组件

项目架构
                        Activity/Fragment
                                |
                      ViewModel(LiveData1/LiveData2/LiveData3)
                                |
                  ----------Repository --------------
                 |                                  |
               Model                      Remote Data source
               (ROOM)                           (Retrofit)
                 |                                  |
               SQLite                            webservice
遵循如上Google Mvvm官方推荐架构，UI与数据分离，以ViewModel为中介进行通信，实现数据驱动UI。通过Koin依赖注入本地数据+远程数据=数据仓库，外部只需一行代码调用，隐藏具体实现，规避数据滥用、后期维护难等问题

lib_base：通用功能组件，支撑业务组件基础，提供其他业务组件实现能力
module_login：业务组件，注册登录模块，以及启动页
module_main：业务组件，app内Tab首页模块
module_project：业务组件，app内Tab项目模块
module_search：功能组件，提供搜索功能
module_square：业务组件，app内Tab广场模块
module_user：业务组件，用户管理以及系统设置模块
module_web：功能组件，提供H5功能


## 4、附加

### 4.1、编译错误解决方法
> 使用databinding其实有个缺点，就是会遇到一些编译错误，而AS不能很好的定位到错误的位置，这对于刚开始使用databinding的开发者来说是一个比较郁闷的事。以下是在开发中遇到的各种编译问题的解决方法分享给大家，希望这对你会有所帮助。

##### 4.1.1、绑定错误
绑定错误是一个很常见的错误，基本都会犯。比如TextView的 `android:text=""` ，本来要绑定的是一个String类型，结果你不小心，可能绑了一个Boolean上去，或者变量名写错了，这时候编辑器不会报红错，而是在点编译运行的时候，在AS的Messages中会出现错误提示，如下图：

<img src="./img/error1.png" width="640" hegiht="640" align=center />

解决方法：把错误提示拉到最下面 (上面的提示找不到BR类这个不要管它)，看最后一个错误 ，这里会提示是哪个xml出了错，并且会定位到行数，按照提示找到对应位置，即可解决该编译错误的问题。

**注意：** 行数要+1，意思是上面报出第33行错误，实际是第34行错误，AS定位的不准确 (这可能是它的一个bug)

##### 4.1.2、xml导包错误
在xml中需要导入ViewModel或者一些业务相关的类，假如在xml中导错了类，那一行则会报红，但是res/layout却没有错误提示，有一种场景，非常特殊，不容易找出错误位置。就是你写了一个xml，导入了一个类，比如XXXUtils，后来因为业务需求，把那个XXXUtils删了，这时候res/layout下不会出现任何错误，而你在编译运行的时候，才会出现错误日志。苦逼的是，不会像上面那样提示哪一个xml文件，哪一行出错了，最后一个错误只是一大片的报错报告。如下图：

解决方法：同样找到最后一个错误提示，找到Cannot resolve type for **xxx**这一句 (xxx是类名)，然后使用全局搜索 (Ctrl+H) ，搜索哪个xml引用了这个类，跟踪点击进去，在xml就会出现一个红错，看到错误你就会明白了，这样就可解决该编译错误的问题。

##### 4.1.3、build错误
构建多module工程时，如出现【4.1.1、绑定错误】，且你能确定这个绑定是没有问题的

解决方法：
这种是databinding比较大的坑，清理、重构和删build都不起作用，网上很难找到方法。经过试验，解决办法是手动创建异常中提到的文件夹，或者拷贝上一个没有报错的版本中对应的文件夹，可以解决这个异常

##### 4.1.4、自动生成类错误
有时候在写完xml时，databinding没有自动生成对应的Binding类及属性。比如新建了一个activity_login.xml，按照databinding的写法加入```<layout> <variable>```后，理论上会自动对应生成ActivityLoginBinding.java类和variable的属性，可能是as对databding的支持还不够吧，有时候偏偏就不生成，导致BR.xxx报红等一些莫名的错误。

解决方法：其实确保自己的写法没有问题，是可以直接运行的，报红不一定是你写的有问题，也有可能是编译器抽风了。或者使用下面的办法</br>
第一招：Build->Clean Project；</br>第二招：Build->Rebuild Project；</br>第三招：重启大法。

##### 4.1.5、gradle错误
如果遇到以下编译问题：

错误: 无法将类 BindingRecyclerViewAdapters中的方法 setAdapter应用到给定类型;
需要: RecyclerView,ItemBinding,List,BindingRecyclerViewAdapter,ItemIds<? super T>,ViewHolderFactory
找到: RecyclerView,ItemBinding,ObservableList,BindingRecyclerViewAdapter<CAP#1>,ItemIds,ViewHolderFactory
原因: 推断类型不符合等式约束条件
推断: CAP#1
等式约束条件: CAP#1,NetWorkItemViewModel
其中, T是类型变量:
T扩展已在方法 setAdapter(RecyclerView,ItemBinding,List,BindingRecyclerViewAdapter,ItemIds<? super T>,ViewHolderFactory)中声明的Object
其中, CAP#1是新类型变量:
CAP#1从?的捕获扩展Object

一般是由于gradle plugin版本3.5.1造成的，请换成gradle plugin 3.5.0以下版本

## 混淆
例子程序中给出了最新的【MVVMHabit混淆规则】，包含MVVMHabit中依赖的所有第三方library，可以将规则直接拷贝到自己app的混淆规则中。在此基础上你只需要关注自己业务代码以及自己引入第三方的混淆，【MVVMHabit混淆规则】请参考app目录下的[proguard-rules.pro](./app/proguard-rules.pro)文件。

## 组件化
进阶Android组件化方案，请移步：[MVVMHabitComponent](https://github.com/goldze/MVVMHabitComponent)
