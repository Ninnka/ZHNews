#ZHNews
`ZHNews`是利用[izzyleung/ZhihuDailyPurify](https://github.com/izzyleung/ZhihuDailyPurify/wiki/%E7%9F%A5%E4%B9%8E%E6%97%A5%E6%8A%A5-API-%E5%88%86%E6%9E%90)提供的的API写的一个第三方知乎日报APP。

这个项目纯粹是拿来练手，最大程度上完成API中所有的功能。

最大程度上依照MaterialDesign来设计APP，这方面还在学习中，日后或许会大改。

对于android5.0及以上的设备可以完全实现状态栏变色，在设计的时候就采用过官方及民间流传的各种方法试过，最后采用了一种自己设计的折中方案，同时也暂时舍弃了android4.4上的状态栏变色效果，这个以后或许会重新支持。

**如果允许的话会发布到平台上提供下载，非商用**


项目简介
-------------------------------
项目所用到的一些方方面面的东西，例如一些第三方框架和一些比较重要的兼容包
* [Glide ](https://github.com/bumptech/glide) 
* [okhttp](https://github.com/square/okhttp)
* [retrofit](https://github.com/square/retrofit)
* [RxJava ](https://github.com/ReactiveX/RxJava)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [Gson](https://github.com/google/gson)
* [SwipeBackLayout](https://github.com/ikew0ng/SwipeBackLayout)
* [RoundedImageView](https://github.com/vinc3m1/RoundedImageView) 
* [commons-lang](https://github.com/apache/commons-lang) 
* [com.android.support:appcompat-v7](https://developer.android.com/topic/libraries/support-library/features.html#v7)
* [com.android.support:support-v4](https://developer.android.com/topic/libraries/support-library/features.html#v4)
* [com.android.support:design](https://developer.android.com/topic/libraries/support-library/features.html#design)
* [com.android.support:cardview-v7](https://developer.android.com/topic/libraries/support-library/features.html#v7-cardview)



完成进度
-------------------------------
* Aug 24, 2016  增加离线缓存
* Aug 20, 2016  添加查看主题日报功能
* Aug 17, 2016  添加查看热门消息功能
* Aug 14 , 2016  添加查看评论功能
* Aug 11, 2016  添加滑动退出当前页面功能
* Aug 10, 2016  添加收藏、点赞、历史查看页面搜索功能
* Aug 05, 2016  添加推送功能，可在设置中打开和关闭，推送功能也可在设置中打开和关闭
* Aug 02, 2016  添加夜间模式
* Jul 29, 2016  添加历史记录清空功能
* Jul 23, 2016  添加“关于页面”和反馈功能
* Jul 19, 2016  添加收藏和点赞页面的多选操作
* Jul 17, 2016  添加历史记录浏览功能
* May 17 - Jul 16 2016 完成APP基本功能，修复各处BUG