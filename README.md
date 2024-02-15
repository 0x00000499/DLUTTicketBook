# DLUT Gymnasium Ticket Rush (大连理工大学体育馆抢票)

**责任声明**：*本仓库提供功能仅供学习交流使用，请勿用于非法用途。如涉及版权权益问题请联系作者删除*。

**提示**：因为仓库中实现的功能如果被滥用的话可能会导致体育场馆预定系统失去存在的意义，所以如果感兴趣的话请联系作者获取使用方法，或者自己研究怎么使用也可以。

# 简介
因为大工体育场馆的位置有限，所以有些热门的场馆比如篮球馆或者羽毛球场馆的预定非常困难。本来资源就不多还有一些需要留给内部人员使用的场地。所以订票就是个很大的问题。所以当前代码实现了在特定时间去代替人力来进行体育场馆预定的功能。 同时为了避免一些内部通道预定对时间选择上造成的干扰，代码也提供了特定时间的可用场地查询功能。总体功能清单如下：
+ 目前支持的场地类型：篮球馆/乒乓球/羽毛球场馆
+ 查询特定日期内空闲时间和空闲场地
+ 自动根据订购日期 即时/定时 发起订购
    - 可以有多种时间组合，比如（12:00，12:30）或者（13:00，13:30）
    - 可以有单个种类多个场地选择，比如羽毛球的 1，2，3 号场地
    - 支持微信/支付宝/玉兰卡支付
  
# 工作流程
## 实现原理
在实际的分析过程中，发现体育场馆的预定系统算是独立于校园门户的一个功能。唯一用到的就是校园门户的认证功能。而且从 i 大工和网页端的界面来看，这个预定系统基本是采用的前后端分离的方式进行开发的。所以可以通过模拟浏览器端发送 HTTP 请求的方式来实现自动订票。这就需要对系统整体的请求过程有一个大致的了解。如果想要自己去看实际请求的过程的话，需要先保证清除所有浏览器的缓存及 Cookie，这样才能反应出一个完整的请求过程。可以通过按 F12 打开开发者工具查看所有的请求。
### 认证过程
体育馆场馆预定的 URL 是：`http://adm-tycg.dlut.edu.cn/api/login/login`
但是如果清除所有浏览器 Cookie 后即没有认证的时候去访问这个链接，会首先要求进行登陆，重定向到达 `http://sso.dlut.edu.cn/cas/login`进行统一身份认证。
当重定向到统一身份认证界面的时候，服务器会自动响应一个 Cookie `JSESSIONCASID`，后续需要携带该 Cookie 进行认证接下来的操作。
![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/20231125151143.png)
输入账号密码之后会进行登陆进入体育场馆预定的主系统，在这个过程中会有两个关键的认证请求。
![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/20231125151214.png)
第一个请求用来申请一个 Cookie`CASTGC`，以及一个服务授权申请 `Ticket`URL。 这个过程中需要用到统一身份认证的账号和密码，作为一个`POST`请求，其请求体中就包含了账号和密码等信息，但是请求过程会对账号密码进行加密，加密的过程在`JS`文件中有所体现，可以自行查看。

![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/1700728281086-dcff0707-7f6a-4f6f-a4d3-f3e06d0ce1a7.png)
![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/1700728175841-8dc653f7-6242-4f55-b0c0-79a8a83a1d29.png)
当拿到带有`Ticket`的 URL 请求之后，直接向该 URL 发送一个 `GET`请求就可以获取到访问场馆预定服务的 token， 后续的所有请求携带该 token 即可。
![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/1700728478244-5758542d-7d01-4b54-9fed-4f3c7c736fef.png)
### 下订单过程
下订单的过程实际上只涉及到两个接口，一个是查询当前的票量剩余，一个是创建订单。上述的两个过程都需要携带第一步所申请到的 token，
查询场地票务信息的接口是`http://adm-tycg.dlut.edu.cn/api/court/getCourtPrice?product_id=78&venue_id=236&date=2023-11-23`。其中有几个查询参数，需要根据查询到场地类型和时间来进行设置。发起该请求后就可以得到各个时间段的场地信息，空闲状态价格等信息。这些信息可以暂时存储起来等到创建订单的时候使用。
![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/1700728706247-f1ffaab9-8312-40f4-8579-a6f01d1b238e.png)
创建订单的接口是`http://adm-tycg.dlut.edu.cn/api/pay/CreateOrder`，这个过程请求头中依旧要携带之前申请到的 token。具体的请求体中有几个参数，可以自行查看其具体的请求内容。其中一个参数是支付方法，**3** 代表的是使用微信/支付宝支付，采用这种方式的话订购成功重定向到付款界面。**1** 代表的是使用玉兰卡支付，如果采用玉兰卡支付的话会直接扣款完成支付过程。
![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/1700729394322-facc7855-d455-40f9-b04c-f07d15e5a529.png)

除此之外还有一个查询接口是用来查询场地的详细信息的其 URL 是`http://adm-tycg.dlut.edu.cn/api/court/getFieldNoList?product_id=80`，比如其名称和 id。因为下订单以及票量查询的时候都是采用的场地的 id，不知道场地的具体名称，为了更好的显示，才有了该接口。
```json
{
    "code": 1,
    "info": "获取成功",
    "data": {
        "result": [
            {
                "id": 342,
                "seat_number": "1号场半场1"
            },
            ...
        ]
    }
}
```
如果运行成功会出现下面的结果，当前采用的是微信/支付宝的支付方式，如果订购成功的话会返回一个支付链接，点击该链接就会进入付款界面，付款后其实就成功订购了。如果订购失败会有信息提示。但是有一点需要注意的是，当前场地预定并没有提供退单的接口和功能，所以如果采用微信支付的方式下了订单但是没有支付的话，个人订单记录里面还是会有这次订单记录。这个人的账户就不能再订购当天这个时间段这个类型场地的票了，其他时间和场地是不影响的。所以微信支付的方式适合用来做功能测试。微信支付抢票成功是下面的结果：
![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/1700792708244-cabb8a67-8a81-4534-87f5-7a6fac749457.png)
如果是选择采用玉兰卡支付的方式，发起订单后会直接从玉兰卡中扣钱，订单会直接完成，所以适用于正式抢票的时候，抢票成功后会显示下面的结果。
![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/1700876981647-569f2dd9-c02c-412c-9f3b-6d7f03fab0f5.png)
### 查询功能
因为可能会有一些校队训练的情况会提前占用一些时间，这些时间是肯定不能被订购的，所以本代码提供了可用时间查询功能。基本原理就是采用了上面的查询票务信息接口，然后对输出数据进行格式化。如果查询成功会得到相应的结果如下图所示：
![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/1700878182689-0946578e-ed06-409a-ab4e-d65d29ca21a9.png)

## 程序流程图
![](https://raw.githubusercontent.com/0x00000499/ImageHostService/main/img/dlutTicketBook/1700725812311-f9480eb9-5a52-4a1c-b745-4ff1f76adb59.jpeg)



