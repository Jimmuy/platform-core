# 使用方式

## Gradle

### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
 allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
```
	
### Step 2. Add the dependency
```
dependencies {
	        implementation 'com.github.Jimmuy:platform:v1.0.3'
	}
```
## Marven
### Step 1. Add the JitPack repository to your build file
```
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://www.jitpack.io</url>
		</repository>
	</repositories>
```
### Step 2. Add the dependency
```
  <dependency>
	    <groupId>com.github.Jimmuy</groupId>
	    <artifactId>platform</artifactId>
	    <version>v1.0.0</version>
	</dependency>
```


## 1. 网络请求部分使用摘要

方便易用，采用api链式调用一点到底,集成cookie管理,极简https配置,上传下载进度显示,请求错误自动重试,请求携带token、时间戳、签名sign动态配置,方便易用的GET,POST形式的请求,3种层次的参数设置默认全局局部,默认标准ApiResult同时可以支持自定义的数据结构以及非标准形式的返回，支持json/xml格式的数据解析

特点：
- 比Retrofit使用更简单
- 采用链式调用一点到底
- 加入基础RequestService，减少Api冗余
- 支持动态配置和自定义底层框架Okhttpclient、Retrofit.
- 支持多种方式访问网络GET、POST。
- 支持固定添加header和动态添加header
- 支持添加全局参数和动态添加局部参数
- 支持文件下载、多文件上传和表单提交数据
- 支持文件请求、上传、下载的进度回调、错误回调，也可以自定义回调
- 支持默认、全局、局部三个层次的配置功能
- 支持任意数据结构的自动解析
- 支持添加动态参数例如timeStamp时间戳、token、签名sign
- 支持自定义的扩展API
- 支持异步、同步请求
- 支持Https、自签名网站Https的访问、双向验证
- 支持失败重试机制，可以指定重试次数、重试间隔时间
- 提供默认的标准ApiResult解析和回调，并且可自定义ApiResult
- 支持取消数据请求，取消订阅，带有对话框的请求不需要手动取消请求，对话框消失会自动取消请求
- 支持请求数据结果采用回调和订阅两种方式
- 返回结果和异常统一处理
- 结合RxJava2，线程智能控制


## 权限说明

因为要请求网络、下载文件到SD卡等等，所以需要在manifest.xml中配置以下几个权限
```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.INTERNET"/>
```

## 默认初始化
```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HttpClient.init(this);//默认初始化
    }
}
```

## 全局配置
可以进行超时配置、okhttp相关参数配置、retrofit相关参数配置、cookie配置等，这些参数可以选择性的根据业务需要配置。全局初始化没有涵盖的配置将使用默认配置，默认配置参数在HttpClient中，每次请求的时候也可以单独配置请求参数，优先级为    单次请求配置 > 全局初始化配置 > 默认全局配置
```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HttpClient.init(this);//默认初始化,必须调用

        //全局设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.put("User-Agent", SystemInfoUtils.getUserAgent(this, AppConstant.APPID));
        //全局设置请求参数
        HttpParams params = new HttpParams();
        params.put("appId", AppConstant.APPID);

        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        HttpClient.getInstance()
        
                //可以全局统一设置全局URL
                .setBaseUrl(Url)//设置全局URL  url只能是域名 或者域名+端口号 

                // 打开该调试开关并设置TAG,不需要就不要加入该行
                // 最后的true表示是否打印内部异常，一般打开方便调试错误
                .debug("HttpClient", true)
                
                //如果使用默认的60秒,以下三行也不需要设置
                .setReadTimeOut(60 * 1000)
                .setWriteTimeOut(60 * 100)
                .setConnectTimeout(60 * 100)
                
                //可以全局统一设置超时重连次数,默认为3次,那么最差的情况会请求4次(一次原始请求,三次重连请求),
                //不需要可以设置为0
                .setRetryCount(3)//网络不好自动重试3次
                //可以全局统一设置超时重试间隔时间,默认为500ms,不需要可以设置为0
                .setRetryDelay(500)//每次延时500ms重试
                //可以全局统一设置超时重试间隔叠加时间,默认为0ms不叠加
                .setRetryIncreaseDelay(500)//每次延时叠加500ms
                //可以设置https的证书,以下几种方案根据需要自己设置
                .setCertificates()                                  //方法一：信任所有证书,不安全有风险
                //.setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
                //配置https的域名匹配规则，不需要就不要加入，使用不当会导致https握手失败
                //.setHostnameVerifier(new SafeHostnameVerifier())
                //.addConverterFactory(GsonConverterFactory.create(gson))//本框架没有采用Retrofit的Gson转化，所以不用配置
                .addCommonHeaders(headers)//设置全局公共头
                .addCommonParams(params)//设置全局公共参数
                //.setCallFactory()//局设置Retrofit对象Factory
                //.setCookieStore()//设置cookie
                //.setOkproxy()//设置全局代理
                //.setOkconnectionPool()//设置请求连接池
                //.setCallbackExecutor()//全局设置Retrofit callbackExecutor
                //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
                //.addInterceptor(new GzipRequestInterceptor())//开启post数据进行gzip后发送给服务器
                .addInterceptor(new CustomSignInterceptor());//添加参数签名拦截器
    }
}
```
### 入口方法
```
  /**
     * get请求
     */
    public static GetRequest get(String url);

    /**
     * post请求和文件上传
     */
    public static PostRequest post(String url);

    /**
     * 自定义请求
     */
    public static CustomRequest custom();

    /**
     * 文件下载
     */
    public static DownloadRequest downLoad(String url) ;

```


### 通用功能配置
1.包含一次普通请求所有能配置的参数，真实使用时不需要配置这么多，按自己的需要选择性的使用即可<br/>
2.以下配置全部是单次请求配置，不会影响全局配置，没有配置的仍然是使用全局参数。<br/>
3.为单个请求设置超时，比如涉及到文件的需要设置读写等待时间多一点。<br/>
完整参数GET示例：
```
HttpClient.get("/v1/app/chairdressing/skinAnalyzePower/skinTestResult")
                .baseUrl("http://www.xxxx.com")//设置url
                .writeTimeOut(30*1000)//局部写超时30s,单位毫秒
                .readTimeOut(30*1000)//局部读超时30s,单位毫秒
                .connectTimeout(30*1000)//局部连接超时30s,单位毫秒
                .headers(new HttpHeaders("header1","header1Value"))//添加请求头参数
                .headers("header2","header2Value")//支持添加多个请求头同时添加
                .headers("header3","header3Value")//支持添加多个请求头同时添加
                .params("param1","param1Value")//支持添加多个参数同时添加
                .params("param2","param2Value")//支持添加多个参数同时添加
                //.addCookie(new CookieManger(this).addCookies())//支持添加Cookie
                //.certificates()添加证书
                .retryCount(5)//本次请求重试次数
                .retryDelay(500)//本次请求重试延迟时间500ms
                .addInterceptor(Interceptor)//添加拦截器
                .okproxy()//设置代理
                .removeHeader("header2")//移除头部header2
                .removeAllHeaders()//移除全部请求头
                .removeParam("param1")
                .accessToken(true)//本次请求是否追加token
                .timeStamp(false)//本次请求是否携带时间戳
                .sign(false)//本次请求是否需要签名
                .syncRequest(true)//是否是同步请求，默认异步请求。true:同步请求
                .xmlRequest(false)//返回数据是否是xml格式，默认按照json格式解析
                .execute(new CallBack<SkinTestResult>() {
                    @Override
                    public void onStart() {
                        //开始请求
                    }

                    @Override
                    public void onCompleted() {
                       //请求完成
                    }

                    @Override
                    public void onError(ApiException e) {
                      //请求错误
                    }

                    @Override
                    public void onSuccess(SkinTestResult response) {
                      //请求成功
                    }
                });
```
#### url
Url可以通过初始化配置的时候传入`HttpClient.getInstance().setBaseUrl("http://www.xxx.com");`  
入口方法传入： `HttpClient.get("/v1/app/chairdressing/skinAnalyzePower/skinTestResult").baseUrl("http://www.xxxx.com")`
如果入口方法中传入的url含有http或者https,则不会拼接初始化设置的baseUrl.
例如：`HttpClient.get("http://www.xxx.com/v1/app/chairdressing/skinAnalyzePower/skinTestResult")`则setBaseUrl()和baseUrl()传入的baseurl都不会被拼接。
*注:HttpClient.get/post/put/等采用拼接的用法时请注意，url要用/斜杠开头，例如：`HttpClient.get("/v1/login")` 正确  ` HttpClient.get("v1/login")` 错误*

`上层开发时建议创建自己的domain管理类来管理有多个base url的情况。`
#### http请求参数
两种设置方式
.params(HttpParams params)
.params("param1","param1Value")//添加参数键值对

 HttpParams params = new HttpParams();
 params.put("appId", AppConstant.APPID);
 .addCommonParams(params)//设置全局公共参数
#### http请求头
.headers(HttpHeaders headers) 
.headers("header2","header2Value")//添加参数键值对

.addCommonHeaders(headers)//设置全局公共头

### 普通网络请求
**支持get/post
链式调用的终点请求的执行方式有：execute(Class<T> clazz) 、execute(Type type)、execute(CallBack<T> callBack)三种方式，都是针对标准的ApiResult
#### execute(CallBack<T> callBack)
1.HttpClient（**推荐**）
示例：
```
方式一：
 //HttpClient.post("/v1/app/chairdressing/skinAnalyzePower/TestResult")
 HttpClient.get("/v1/app/chairdressing/skinAnalyzePower/TestResult")
                .readTimeOut(30 * 1000)//局部定义读超时
                .writeTimeOut(30 * 1000)
                .connectTimeout(30 * 1000)
                .params("name","张三")
                .timeStamp(true)
                .execute(new SimpleCallBack<TestResult>() {
                    @Override
                    public void onError(ApiException e) {
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(TestResult response) {
                        if (response != null) showToast(response.toString());
                    }
                });
```
2.手动创建请求对象
```
 //GetRequest 、PostRequest、DeleteRequest、PutRequest
 GetRequest request = new GetRequest("/v1/app/chairdressing/skinAnalyzePower/TestResult");
        request.readTimeOut(30 * 1000)//局部定义读超时
                .params("param1", "param1Value1")
                .execute(new SimpleCallBack<TestResult>() {
                    @Override
                    public void onError(ApiException e) {

                    }

                    @Override
                    public void onSuccess(TestResult response) {

                    }
                });
```
#### execute(Class<T> clazz)和execute(Type type)
execute(Class<T> clazz)和execute(Type type)功能基本一样，execute(Type type)主要是针对集合不能直接传递Class
```
HttpClient.get(url)
                .params("param1", "paramValue1")
                .execute(SkinTestResult.class)//非常简单直接传目标class
                //.execute(new TypeToken<List<SectionItem>>() {}.getType())//Type类型
                .subscribe(new BaseSubscriber<SkinTestResult>() {
                    @Override
                    public void onError(ApiException e) {
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onNext(SkinTestResult skinTestResult) {
                        showToast(skinTestResult.toString());
                    }
                });
```
### 请求返回Disposable
网络请求会返回Disposable对象，方便取消网络请求
```
Disposable disposable = HttpClient.get("/v1/app/chairdressing/skinAnalyzePower/skinTestResult")
                .params("param1", "paramValue1")
                .execute(new SimpleCallBack<SkinTestResult>() {
                    @Override
                    public void onError(ApiException e) {
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(SkinTestResult response) {
                        showToast(response.toString());
                    }
                });

        //在需要取消网络请求的地方调用,一般在onDestroy()中
        //HttpClient.cancelSubscription(disposable);
```
### 带有进度框的请求
带有进度框的请求，可以设置对话框消失是否自动取消网络和自定义对话框功能
#### 方式一：ProgressDialogCallBack
ProgressDialogCallBack带有进度框的请求，可以设置对话框消失是否自动取消网络和自定义对话框功能，具体参数作用请看自定义CallBack讲解
```
 IProgressDialog mProgressDialog = new IProgressDialog() {
            @Override
            public Dialog getDialog() {
                ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("请稍候...");
                return dialog;
            }
        };
        HttpClient.get("/v1/app/chairdressing/")
                .params("param1", "paramValue1")
                .execute(new ProgressDialogCallBack<SkinTestResult>(mProgressDialog, true, true) {
                    @Override
                    public void onError(ApiException e) {
                        super.onError(e);//super.onError(e)必须写不能删掉或者忘记了
                        //请求失败
                    }

                    @Override
                    public void onSuccess(SkinTestResult response) {
                       //请求成功
                    }
                });
```
*注：错误回调 super.onError(e);必须写*
#### 方式二：ProgressSubscriber

```
IProgressDialog mProgressDialog = new IProgressDialog() {
            @Override
            public Dialog getDialog() {
                ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("请稍候...");
                return dialog;
            }
        };
 HttpClient.get(URL)
                .timeStamp(true)
                .execute(SkinTestResult.class)
                .subscribe(new ProgressSubscriber<SkinTestResult>(this, mProgressDialog) {
                    @Override
                    public void onError(ApiException e) {
                        super.onError(e);
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onNext(SkinTestResult skinTestResult) {
                        showToast(skinTestResult.toString());
                    }
                });

```

### 请求返回Observable
通过网络请求可以返回Observable，这样就可以很好的通过Rxjava与其它场景业务结合处理，甚至可以通过Rxjava的connect()操作符处理多个网络请求。例如：在一个页面有多个网络请求，如何在多个请求都访问成功后再显示页面呢？这也是Rxjava强大之处。
*注：目前通过execute(Class<T> clazz)方式只支持标注的ApiResult结构，不支持自定义的ApiResult*
示例：
```
Observable<SkinTestResult> observable = HttpClient.get(url)
                .params("param1", "paramValue1")
                .execute(SkinTestResult.class);

        observable.subscribe(new BaseSubscriber<SkinTestResult>() {
            @Override
            public void onError(ApiException e) {
                showToast(e.getMessage());
            }

            @Override
            public void onNext(SkinTestResult skinTestResult) {
                showToast(skinTestResult.toString());
            }
        });
```

### 文件下载
本库提供的文件下载非常简单，没有提供复杂的下载方式例如：下载管理器、断点续传、多线程下载等
文件目录如果不指定,默认下载的目录为/storage/emulated/0/Android/data/包名/files
文件名如果不指定,则按照以下规则命名:
>1.首先检查用户是否传入了文件名,如果传入,将以用户传入的文件名命名
>2.如果没有传入文件名，默认名字是时间戳生成的。
>3.如果传入了文件名但是没有后缀，程序会自动解析类型追加后缀名

示例：
```
 String url = "http://61.144.207.146:8081/b8154d3d-4166-4561-ad8d-7188a96eb195/2005/07/6c/076ce42f-3a78-4b5b-9aae-3c2959b7b1ba/kfid/2475751/qqlite_3.5.0.660_android_r108360_GuanWang_537047121_release_10000484.apk";
        HttpClient.downLoad(url)
                .savePath("/sdcard")
                .saveName("release_10000484.apk")//不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        int progress = (int) (bytesRead * 100 / contentLength);
                        HttpLog.e(progress + "% ");
                        dialog.setProgress(progress);
                        if (done) {//下载完成
                        }
                        ...
                    }

                    @Override
                    public void onStart() {
                       //开始下载
                    }

                    @Override
                    public void onComplete(String path) {
                       //下载完成，path：下载文件保存的完整路径
                    }

                    @Override
                    public void onError(ApiException e) {
                        //下载失败
                    }
                });
```

### POST请求，上传String、json、object、body、byte[]
一般此种用法用于与服务器约定的数据格式，当使用该方法时，params中的参数设置是无效的，所有参数均需要通过需要上传的文本中指定，此外，额外指定的header参数仍然保持有效。
- `.upString("这是要上传的长文本数据！")//默认类型是：MediaType.parse("text/plain")`
- 如果你对请求头有自己的要求，可以使用这个重载的形式，传入自定义的content-type文本
 `upString("这是要上传的长文本数据！", "application/xml") // 比如上传xml数据，这里就可以自己指定请求头`
- upJson该方法与upString没有本质区别，只是数据格式是json,通常需要自己创建一个实体bean或者一个map，把需要的参数设置进去，然后通过三方的Gson或者 fastjson转换成json字符串，最后直接使用该方法提交到服务器,支持三种格式的请求参数上传。
’‘’
    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    public R upJson(Map<String, Object> params) {
        return upJson(GsonConverter.toJson(params));
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    public R upJson(JsonRequestParam... jsonRequestParams) {
        Map<String, Object> map = new HashMap<>();
        for (JsonRequestParam param : jsonRequestParams) {
            map.put(param.getKey(), param.getValue());
        }
        return upJson(GsonConverter.toJson(map));
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除,必须是键值对的参数类型
     */
    public R upJson(Object param) {

        return upJson(GsonConverter.toJson(param));
    }
‘’‘
`.upJson(jsonObject.toString())//上传json`
 //.upJson(new JsonRequestParam("username", "admin"), new JsonRequestParam("password", "123"))
 //.upJson(new RequestObject)
 //.upJson(new HashMap<String,Object>())
- `.upBytes(new byte[]{})//上传byte[]`
- `.requestBody(body)//上传自定义RequestBody`
- `.upObject(object)//上传对象object`   必须要增加`.addConverterFactory(GsonConverterFactory.create())`设置


> 1.upString、upJson、requestBody、upBytes、upObject五个方法不能同时使用，当前只能选用一个
> 2.使用upJson、upObject时候params、sign(true/false)、accessToken（true/false）、拦截器都不会起作用
> 解析返回的数据，目前提供了json/xml两种格式的数据返回解析，使用时只需要设置xmlRequest（true）就可以解析xml，默认为json解析。


示例：
``` 
HashMap<String, String> params = new HashMap<>();
params.put("key1", "value1");
params.put("key2", "这里是需要提交的json格式数据");
params.put("key3", "也可以使用三方工具将对象转成json字符串");
JSONObject jsonObject = new JSONObject(params);

RequestBody body=RequestBody.create(MediaType.parse("xxx/xx"),"内容");
HttpClient.post("v1/app/chairdressing/news/favorite")
                //.params("param1", "paramValue1")//不能使用params，upString 与 params 是互斥的，只有 upString 的数据会被上传
                .upString("这里是要上传的文本！")//默认类型是：MediaType.parse("text/plain")
                //.upString("这是要上传的长文本数据！", "application/xml") // 比如上传xml数据，这里就可以自己指定请求头
                
                 //.upJson(jsonObject.toString())
                 //.requestBody(body)
                 //.upBytes(new byte[]{})
                 //.upObject(object)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        showToast(response);
                    }
                });

```

### 上传图片或者文件
>支持单文件上传、多文件上传、混合上传，同时支持进度回调，
>暂不实现多线程上传/分片上传/断点续传等高级功能

上传文件支持文件与参数一起同时上传，也支持一个key上传多个文件，以下方式可以任选
上传文件支持两种进度回调：ProgressResponseCallBack(线程中回调)和UIProgressResponseCallBack（可以刷新UI）
```
final UIProgressResponseCallBack listener = new UIProgressResponseCallBack() {
            @Override
            public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                int progress = (int) (bytesRead * 100 / contentLength);
                if (done) {//完成
                }
                ...
            }
        };
        HttpClient.post("/v1/user/uploadAvatar")
                //支持上传新增的参数
                //.params(String key, File file, ProgressResponseCallBack responseCallBack)
                //.params(String key, InputStream stream, String fileName, ProgressResponseCallBack responseCallBack)
                //.params(String key, byte[] bytes, String fileName, ProgressResponseCallBack responseCallBack) 
                //.addFileParams(String key, List<File> files, ProgressResponseCallBack responseCallBack)
                //.addFileWrapperParams(String key, List<HttpParams.FileWrapper> fileWrappers)
                //.params(String key, File file, String fileName, ProgressResponseCallBack responseCallBack)
                //.params(String key, T file, String fileName, MediaType contentType, ProgressResponseCallBack responseCallBack)
                
                //方式一：文件上传
                File file = new File("/sdcard/1.jpg");
                //如果有文件名字可以不用再传Type,会自动解析到是image/*
                .params("avatar", file, file.getName(), listener)
                //.params("avatar", file, file.getName(),MediaType.parse("image/*"), listener)

                //方式二：InputStream上传
               final InputStream inputStream = getResources().getAssets().open("1.jpg");
                .params("avatar", inputStream, "test.png", listener)
                
                //方式三：byte[]上传
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                final byte[] bytes = baos.toByteArray();
                //.params("avatar",bytes,"streamfile.png",MediaType.parse("image/*"),listener)
                //如果有文件名字可以不用再传Type,会自动解析到是image/*
                .params("avatar", bytes, "streamfile.png", listener)
        
                .params("file1", new File("filepath1"))   // 可以添加文件上传
	            .params("file2", new File("filepath2")) 	// 支持多文件同时添加上传
	            .addFileParams("key", List<File> files)	// 这里支持一个key传多个文件
                .params("param1", "paramValue1") 		// 这里可以上传参数
                .accessToken(true)
                .timeStamp(true)
                .execute(new ProgressDialogCallBack<String>(mProgressDialog, true, true) {
                    @Override
                    public void onError(ApiException e) {
                        super.onError(e);
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {
                        showToast(response);
                    }
                });
```
### 取消请求
#### 通过Disposable取消
每个请求前都会返回一个Disposable，取消订阅就可以取消网络请求，如果是带有进度框的网络请求，则不需要手动取消网络请求，会自动取消。
```
 Disposable mSubscription = HttpClient.get(url).execute(callback);
  ...
  @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpClient.cancelSubscription(mSubscription);
    }
```
#### 通过dialog取消
自动取消使用ProgressDialogCallBack回调或者使用ProgressSubscriber,就不用再手动调用cancelSubscription();
ProgressDialogCallBack:
 ```
HttpClient.get(url).execute(new ProgressDialogCallBack());
```
ProgressSubscriber
```
HttpClient.get(url).execute(SkinTestResult.class).subscribe(new ProgressSubscriber<SkinTestResult>(this, mProgressDialog) {
            @Override
            public void onError(ApiException e) {
                super.onError(e);
                showToast(e.getMessage());
            }

            @Override
            public void onNext(SkinTestResult skinTestResult) {
                showToast(skinTestResult.toString());
            }
        })
```

### 同步请求
同步请求只需要设置syncRequest()方法
```
 HttpClient.get("/v1/app/chairdressing/skinAnalyzePower/skinTestResult")
                ...
                .syncRequest(true)//设置同步请求
                .execute(new CallBack<SkinTestResult>() {});
```

### 请求回调CallBack支持的类型
```
//支持回调的类型可以是Bean、String、List<Bean>
new SimpleCallBack<Bean>()//返回Bean
new SimpleCallBack<String>()//返回字符串
new SimpleCallBack<List<Bean>()//返回集合
```
*注：其它回调同理*

### cookie使用
cookie的内容主要包括：名字，值，过期时间，路径和域。路径与域一起构成cookie的作用范围
cookie设置：
```
HttpClient.getInstance()
   				 ...
                  //如果不想让本库管理cookie,以下不需要
                .setCookieStore(new CookieManger(this)) //cookie持久化存储，如果cookie不过期，则一直有效
                 ...
```

- 查看url所对应的cookie

```
HttpUrl httpUrl = HttpUrl.parse("http://www.xxx.com/test");
CookieManger cookieManger = getCookieJar();
List<Cookie> cookies =  cookieManger.loadForRequest(httpUrl);
```

- 查看CookieManger所有cookie
 
```
PersistentCookieStore cookieStore= getCookieJar().getCookieStore();
List<Cookie> cookies1= cookieStore.getCookies();
```

- 添加cookie

```
Cookie.Builder builder = new Cookie.Builder();
Cookie cookie = builder.name("mCookieKey1").value("mCookieValue1").domain(httpUrl.host()).build();
CookieManger cookieManger = getCookieJar();
cookieManger.saveFromResponse(httpUrl, cookie);
//cookieStore.saveFromResponse(httpUrl, cookieList);//添加cookie集合
```

- 移除cookie

```
HttpUrl httpUrl = HttpUrl.parse("http://www.xxx.com/test");
CookieManger cookieManger = HttpClient.getCookieJar();
Cookie cookie = builder.name("mCookieKey1").value("mCookieValue1").domain(httpUrl.host()).build();
cookieManger.remove(httpUrl,cookie);
```

- 清空cookie

```
CookieManger cookieManger = HttpClient.getCookieJar();
cookieManger.removeAll();
```

### 自定义call()请求
提供了用户自定义RequestService的接口，您只需调用call方法即可.
示例：
```
public interface LoginService {
    @POST("{path}")
    @FormUrlEncoded
    Observable<ApiResult<AuthModel>> login(@Path("path") String path, @FieldMap Map<String, String> map);
}

final CustomRequest request = HttpClient.custom()
                .addConverterFactory(GsonConverterFactory.create(new Gson()))//自定义的可以设置GsonConverterFactory
                .params("param1", "paramValue1")
                .build();

        LoginService mLoginService = request.create(LoginService.class);
        LoginService mLoginService = request.create(LoginService.class);
        Observable<ApiResult<AuthModel>> observable = request.call(mLoginService.login("v1/account/login", request.getParams().urlParamsMap));
        Disposable subscription = observable.subscribe(new Action1<ApiResult<AuthModel>>() {
            @Override
            public void call(ApiResult<AuthModel> result) {
                //请求成功
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //请求失败
            }
        });
```

### 自定义apiCall()请求
提供默认的支持ApiResult结构，数据返回不需要带ApiResult,直接返回目标.
示例：
```
Observable<AuthModel> observable = request.apiCall(mLoginService.login("v1/account/login", request.getParams().urlParamsMap));
```
## 请求回调和订阅
请求回调本库提供两种方式Callback和Subscriber
### 回调方式
此种方式主要针对execute(CallBack<T> callBack)，目前内部提供的回调包含CallBack, SimpleCallBack ,ProgressDialogCallBack ,DownloadProgressCallBack 可以根据自己的需求去自定义Callback

- CallBack所有回调的基类，抽象类
- SimpleCallBack简单回调，只有成功和失败
- ProgressDialogCallBack带有进度框的回调，可以自定义进度框、支持是否可以取消对话框、对话框消失自动取消网络请求等参数设置
- DownloadProgressCallBack如果要做文件下载，则必须使用该回调，内部封装了关于文件下载进度回调的方法，如果使用其他回调也可以，但是没有进度通知

该网络框架的核心使用方法即为Callback的继承使用，因为不同的项目需求，会有个性化的回调请自定义
#### CallBack回调
```
new CallBack<T>() {
                    @Override
                    public void onStart() {
                       //请求开始
                    }

                    @Override
                    public void onCompleted() {
                       //请求完成
                    }

                    @Override
                    public void onError(ApiException e) {
                       //请求失败
                    }

                    @Override
                    public void onSuccess(T t) {
                       //请求成功
                    }
                }
```

#### SimpleCallBack回调

```
new SimpleCallBack<T>() {
                    @Override
                    public void onError(ApiException e) {
                         //请求失败
                    }

                    @Override
                    public void onSuccess(T t) {
                        //请求成功
                    }
                }
```

#### ProgressDialogCallBack回调
可以自定义带有加载进度框的回调，取消对话框会自动取消掉网络请求

提供两个构造
> public ProgressDialogCallBack(IProgressDialog progressDialog);//默认不能取消对话框
> public ProgressDialogCallBack(IProgressDialog progressDialog, boolean isShowProgress, boolean isCancel);//自定义加载进度框,可以设置是否显示弹出框，是否可以取消 progressDialog: dialog对象接口  isShowProgress：对话框消失是否取消网络请求 isCancel：是否可以取消对话框对应Dialog的setCancelable(isCancel)方法;

自定义ProgressDialog对话框
```
 private IProgressDialog mProgressDialog = new IProgressDialog() {
        @Override
        public Dialog getDialog() {
            ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("请稍候...");
            return dialog;
        }
    };
```
#### DownloadProgressCallBack回调
此回调只用于文件下载，具体请看文件下载讲解
#### 自定义CallBack回调
如果对回调有特殊需求，支持可以继承CallBack自己扩展功能

### 订阅方式
此种方式主要是针对execute(Class<T> clazz)和execute(Type type)，目前内部提供的Subscriber包含BaseSubscriber、DownloadSubscriber、ProgressSubscriber，可以根据自己的需求去自定义Subscriber
- BaseSubscriber所有订阅者的基类，抽象类
- DownloadSubscriber下载的订阅者，上层不需要关注
- ProgressSubscriber带有进度框的订阅，可以自定义进度框、支持是否可以取消对话框、对话框消失自动取消网络请求等参数设置

```
new BaseSubscriber<T>() {
            @Override
            public void onError(ApiException e) {
               //请求失败
            }

            @Override
            public void onNext(T t) {
                //请求成功
            }
        }
```

```
new ProgressSubscriber<T>(this, mProgressDialog) {
                    @Override
                    public void onError(ApiException e) {
                        super.onError(e);
                        //请求失败
                    }

                    @Override
                    public void onNext(T t) {
                         //请求成功
                    }
                }
```

### 自定义Subscriber
如果对Subscriber有特殊需求，支持可以继承BaseSubscriber自己扩展订阅者

## 动态参数
动态参数就是像我们的token、时间戳timeStamp、签名sign等，这些参数不能是全局参数因为是变化的，设置成局部参数又太麻烦，每次都要获取。token是有有效时间的或者异地登录等都会变化重新获取，时间戳一般是根据系统的时间，sign是根据请求的url和参数进行加密签名一般都有自己的签名规则。
#### 1.在请求的时候可以设置下面三个参数
```
.accessToken(true)//本次请求是否追加token
.timeStamp(false)//本次请求是否携带时间戳
.sign(false)//本次请求是否需要签名
```
#### 2.需要继承库中提供的动态拦截器BaseDynamicInterceptor
继承BaseDynamicInterceptor后就可以获取到参数的设置值
示例:
```
/**
 * <p>描述：对参数进行签名、添加token、时间戳处理的拦截器</p>
 * 主要功能说明：<br>
 * 因为参数签名没办法统一，签名的规则不一样，签名加密的方式也不同有MD5、BASE64等等，只提供自己能够扩展的能力。<br>
 */
public class CustomSignInterceptor extends BaseDynamicInterceptor<CustomSignInterceptor> {
    @Override
    public TreeMap<String, String> dynamic(TreeMap<String, String> dynamicMap) {
        //dynamicMap:是原有的全局参数+局部参数
        //你不必关心当前是get/post/上传文件/混合上传等，库中会自动帮你处理。
        //根据需要自己处理，如果你只用到token则不必处理isTimeStamp()、isSign()
        if (isTimeStamp()) {//是否添加时间戳，因为你的字段key可能不是timestamp,这种动态的自己处理
            dynamicMap.put(ComParamContact.Common.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        }
        if (isSign()) {是否签名
            //1.因为你的字段key可能不是sign，这种需要动态的自己处理
            //2.因为你的签名的规则不一样，签名加密方式也不一样，只提供自己能够扩展的能力
            dynamicMap.put(ComParamContact.Common.SIGN, sign(dynamicMap));
        }
        if (isAccessToken()) {//是否添加token
            String acccess = TokenManager.getInstance().getAuthModel().getAccessToken();
            dynamicMap.put(ComParamContact.Common.ACCESSTOKEN, acccess);
        }
        //Logc.i("dynamicMap:" + dynamicMap.toString());
        return dynamicMap;//dynamicMap:是原有的全局参数+局部参数+新增的动态参数
    }

    //示例->签名规则：POST+url+参数的拼装+secret
    private String sign(TreeMap<String, String> dynamicMap) {
        String url = getHttpUrl().url().toString();
        url = url.replaceAll("%2F", "/");
        StringBuilder sb = new StringBuilder("POST");
        sb.append(url);
        for (Map.Entry<String, String> entry : dynamicMap.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        sb.append(AppConstant.APP_SECRET);
        HttpLog.i(sb.toString());
        return MD5.encode(sb.toString());
    }
}
```
#### 3.设置自定义的动态拦截器
最好通过全局的方式设置，因为一般很多接口都会使用到
```
 HttpClient.getInstance()
                 ...
                .addInterceptor(new CustomSignInterceptor())//添加动态参数（签名、token、时间戳）拦截器
                 ...
```
## 自定义ApiResult
本库中默认提供的是标准ApiResult.内部是靠ApiResult进行解析的，如果你的数据结构跟ApiResult不同，你可以在你的项目中继承ApiResult，然后重写getCode()、getData()、getMsg()和isOk()等方法来实现自己的需求。
本库中ApiResult如下：
```
public class ApiResult<T> {
    private int code;
    private String msg;
    private T data;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isOk() {//请求成功的判断方法
        return code == 0 ? true : false;
    }
}
```
json格式类似:
```
{
"code": 100010101,
"data": 内容,
"msg": "请求成功"
}
```
假如你的数据结构是这样的：
```
{
"error_code": 0,
"result": 内容,
"reason": "请求成功"
}
```
那么你的basebean可以写成这样
```
public class CustomApiResult<T> extends ApiResult<T> {
    String reason;
    int error_code;
    //int resultcode;
    T result;
    @Override
    public int getCode() {
        return error_code;
    }
    @Override
    public void setCode(int code) {
        error_code = code;
    }
    @Override
    public String getMsg() {
        return reason;
    }
    @Override
    public void setMsg(String msg) {
        reason = msg;
    }
    @Override
    public T getData() {
        return result;
    }
    @Override
    public void setData(T data) {
        result = data;
    }
   /* @Override
    public boolean isOk() {
        return error_code==200;//如果不是0表示成功，请重写isOk()方法。
    }*/
}
```
假如你的数据结构很变态是这样的：
```
{
"datas": 我只返回data没有code和msg你打我啊,
}
```
那么你的网络请求可以这样写
```
public class UnstandardApiResult<T> extends ApiResult<T>{
     T datas;
    @Override
    public T getData() {
        return datas;
    }
    @Override
    public boolean isOk() {
        return datas != null;//自己定义成功
    }
    @Override
    public String getMsg() {
        return "xxx";
    }
    @Override
    public int getCode() {
        return 200;
    }
}
```
##### 自定义ApiResult回调方式（通过CallBackProxy代理）
```
HttpClient.get(url)
                .readTimeOut(30 * 1000)//局部定义读超时
                .writeTimeOut(30 * 1000)
                .connectTimeout(30 * 1000)
                //.headers("","")//设置头参数
                //.params("name","张三")//设置参数
                //.addInterceptor()
                //.addConverterFactory()
                //.addCookie()
                //.timeStamp(true)
                .baseUrl("http://apis.juhe.cn")
                .params("phone", "手机号")
                .params("dtype", "json")
                .params("key", "5682c1f44a7f486e40f9720d6c97ffe4")
                .execute(new CallBackProxy<CustomApiResult<ResultBean>, ResultBean>(new SimpleCallBack<ResultBean>() {
                    @Override
                    public void onError(ApiException e) {
                        //请求错误
                    }

                    @Override
                    public void onSuccess(ResultBean response) {
                        //请求成功
                    }
                }) {
                });
```

这种写法会觉得有点长，CallBackProxy的泛型参数每次都需要填写，其中CustomApiResult是继承ApiResult的，CustomApiResult相当于项目的basebean,对于一个实际项目来讲，basebean是固定的，所以我们可以继续封装这个方法，根据需要一般只需要封装get和post请求就可以了。
```
 public static <T> Disposable customExecute(CallBack<T> callBack) {
        return execute(new CallBackProxy<CustomApiResult<T>, T>(callBack) {
        });
    }
```

通过以上改造，再次调用时直接使用CallBack，不用再关注CallBackProxy
##### 自定义ApiResult订阅方式（通过CallClazzProxy代理）
```
Observable<ResultBean> observable = HttpClient.get("/mobile/get")
                .readTimeOut(30 * 1000)//局部定义读超时
                .writeTimeOut(30 * 1000)
                .baseUrl("http://apis.juhe.cn")
                .params("phone", "18688994275")
                .params("dtype", "json")
                .params("key", "5682c1f44a7f486e40f9720d6c97ffe4")
                .execute(new CallClazzProxy<CustomApiResult<ResultBean>, ResultBean>(ResultBean.class) {
                });
        observable.subscribe(new ProgressSubscriber<ResultBean>(this, mProgressDialog) {
            @Override
            public void onError(ApiException e) {
                super.onError(e);
                showToast(e.getMessage());
            }

            @Override
            public void onNext(ResultBean result) {
                showToast(result.toString());
            }
        });
```
##### 上层使用封装
由于底层框架只能适配标准的网络请求返回，虽然也支持非标准的网络请求，但是写法复杂，通过上层简单封装，会让调用变得简单清晰。现在假设上层并不是标准的ApiResult,例如
```
public class HSApiResult<T> extends ApiResult<T> {

    public T data;
    public Boolean success;
    public String  errorMsg;
    public String  errorCode;

    @Override
    public T getBaseData() {
        return data;
    }

    @Override
    public boolean isOk() {
        return success;
    }

    @Override
    public String getMsg() {
        return errorMsg;
    }

    @Override
    public int getCode() {
        return Integer.valueOf(errorCode);
    }
}
```
上层封装的时候可以，使用自定义的GetRequest和PostRequest来简单封装，例如
```
public class HSGetRequest extends GetRequest {
    public HSGetRequest(String url) {
        super(url);
    }
    
    @Override
    public <T> Observable<T> execute(Type type) {
        return super.execute(new CallClazzProxy<HSApiResult<T>, T>(type) {
        });
    }

    @Override
    public <T> Observable<T> execute(Class<T> clazz) {
        return super.execute(new CallClazzProxy<HSApiResult<T>, T>(clazz) {
        });
    }

    @Override
    public <T> Disposable execute(CallBack<T> callBack) {
        return super.execute(new CallBackProxy<HSApiResult<T>, T>(callBack) {
        });
    }
}
```
```

public class HSPostRequest extends PostRequest {
    public HSPostRequest(String url) {
        super(url);
    }

    @Override
    public <T> Observable<T> execute(Type type) {
        return super.execute(new CallClazzProxy<HSApiResult<T>, T>(type) {
        });
    }

    @Override
    public <T> Observable<T> execute(Class<T> clazz) {
        return super.execute(new CallClazzProxy<HSApiResult<T>, T>(clazz) {
        });
    }

    @Override
    public <T> Disposable execute(CallBack<T> callBack) {
        return super.execute(new CallBackProxy<HSApiResult<T>, T>(callBack) {
        });
    }
}
```
最后创建一个HttpManager来管理新的GetRequest和PostRequest
```
public class HttpManager {
    /**
     * get请求
     */
    public static GetRequest get(String url) {
        return new HSGetRequest(url);
    }

    /**
     * post请求
     */
    public static PostRequest post(String url) {
        return new HSPostRequest(url);
    }
}

```
最终的使用形式为,其他使用方式不变，参照文档介绍即可
```
    HashMap<String, Object> params = new HashMap<>();
        params.put("xxx", xxx);
        params.put("xxx", xxx);
        HttpManager.post("http:xxx.xxx.xx").upJson(params).execute(new SimpleCallBack<XxxxModel>() {
            @Override
            public void onError(ApiException e) {
                //"failed"
            }

            @Override
            public void onSuccess(XxxxModel model) {
                //"success"
            }
        });
```
## 调试模式
调试模式的控制在初始化配置时就可以直接设置。
```
public class MyApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            ...
            HttpClient.getInstance()
            		...
                    // 打开该调试开关并设置TAG,不需要就不要加入该行
                    // 最后的true表示是否打印内部异常，一般打开方便调试错误
                    .debug("HttpClient", true);
        }
    }
```
#### Log预览说明
一个请求的Log有以下特点：
1.开头和结尾打了-->http is start和 -->http is Complete分割请求，完整的生命周期的内容都会打印在开头和结尾的里面。
2.request请求和response响应分割，分别是
> -------------------------------request-------------------------------

> -------------------------------response-------------------------------

3.在---request---之后会打印请求的url、当前请求的类型GET/POST... -->GET/POST开头  -->END GET/POST结尾。如果是GET、HEAD请求方式添加的参数将会在这里完整的以url?key=value&key=value的形式打印。
4.在----response----之后会打印（在服务器响应后被打印），包含响应码、响应状态、响应头、cookie,body等以<--200(响应码)开头，<--END HTTP结尾
##### Log本地打印说明
由于上层可以自定义拦截器来添加头信息等，会造成框架内集成的打印器打印日志不全等问题，所以不推荐在框架内提供日志打印到本地的功能，如果有相关的需求，可以自定义一个拦截器在其中做相应的操作即可。
## 混淆

```
#okhttp
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

# Retrolambda
-dontwarn java.lang.invoke.*

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
###rxandroid-1.2.1
-keepclassmembers class rx.android.**{*;}

# Gson
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod
-keep class org.xz_sale.entity.**{*;}
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#HttpClient
-keep class xxxxx//自己定义的model
```
