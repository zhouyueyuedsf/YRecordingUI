# YRecordingUI(版本号：0.9）
#### 效果
![img](https://i.loli.net/2018/12/11/5c0f50a3caa92.gif)

#### 目的

看见Android市场上，录音的UI界面及可操作性，我只想说一句：”难受呀，马飞“，但是当我拿着室友的iPhone手机看了下录音界面，尼玛，必须自己写一个了，同时也可以锻炼一下

#### 介绍

一款易扩展，易用，且具有较好性能的UI界面

#### 用法

##### xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
        <com.yueyue_projects.library.TimeHorizontalScrollView
            android:id="@+id/time_scroll_view"
            android:layout_width="match_parent"
            android:layout_height="150dp"/>
</android.support.constraint.ConstraintLayout>
```

##### java

```java
TimeHorizontalScrollView scrollView =(TimeHorizontalScrollView)findViewById(R.id.time_scroll_view);
// 可以自己渲染录音的动效，也可以使用默认的，renderDatas就是你要渲染的数据，录音一般为声音大小
 scrollView.setITextureRenderer(new ITextureRenderer() {
            @Override
            public void draw(Canvas canvas, int renderStartPx, int renderPivotPx, int renderEndPx,
                             int renderHeight, SparseArray<Integer> renderDatas) {
            }
        });
//模拟的录音数据设置
 scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
              	//你只需要start，并返回渲染数据即可，在录音停止时请调用stop
                scrollView.start();
                long t = 0;
                for (int i = 0; i < 500; i++) {
                    scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                          	// 设置一帧数据，目前仅需要设置音量大小即可(0~100)
                            scrollView.setFrameData((int) (Math.random() * 11) + 10);
                        }
                    }, t);
                    t += 20;
                }
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.stop();
                    }
                }, 10 * 1000);
            }
        }, 1000);
```







