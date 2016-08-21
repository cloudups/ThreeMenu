ThreeMenu
===

忘记哪年哪月哪日，在dribble上看到的这个beautiful Android menu，今天抽空实现了~ <br>
废话不多说，上图~~ <br><br>
<img src="./ThreeMenu/gif/menu.gif" width = "320" height = "240" alt="ThreeMenu" />
<br><br>


# [apk样例下载](./ThreeMenu/apk/ThreeMenuDemo.apk?raw=true)
<br><br>

# 如何使用

### 0x00
- 把源文件拷到你的  _src/包名_  目录下.

### 0x01
- 在布局文件中加入该组件，如下所示： <br>

```
  <com.feiqishi.customview.ThreeMenu
        android:id="@+id/view"
        android:layout_width="70dp"
        android:layout_height="70dp" >
  </com.feiqishi.customview.ThreeMenu>
```

### 0x02
- 在你的代码中引用该组件：<br>

```
    ThreeMenu view = (ThreeMenu) findViewById(R.id.view);
    view.postive();//打开menu
    view.negative();//关闭menu
```

### 0x03
- 当然你也可以动态加载~~~
<br><br>

# 感谢
https://dribbble.com/shots/1623679-Open-Close
<br><br>

# 注意
请原谅我只在工程目录下放了组件源码。<br>
如果你对该项目有什么问题或想法，请联系我：feiqishi@foxmail.com
