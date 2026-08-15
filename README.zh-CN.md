<p align="center">
  <img src="https://img.shields.io/badge/Java-8+-orange?logo=java" alt="Java 8+"/>
  <img src="https://img.shields.io/badge/MySQL-8.0+-blue?logo=mysql" alt="MySQL 8.0+"/>
  <img src="https://img.shields.io/badge/UI-Swing-4B8BF4" alt="Swing"/>
  <img src="https://img.shields.io/badge/Data-JDBC-brightgreen" alt="JDBC"/>
  <img src="https://img.shields.io/badge/License-MIT-lightgrey" alt="License MIT"/>
  <img src="https://img.shields.io/badge/release-v1.0-blue" alt="Release v1.0"/>
</p>

<h1 align="center">鱼眼电影</h1>

<p align="center">
  基于 <strong>Java Swing + JDBC + MySQL</strong> 的桌面端 <strong>影院售票与管理系统</strong>。
</p>

<p align="center">
  <a href="#安装运行">安装运行</a> ·
  <a href="#项目简介">项目简介</a> ·
  <a href="#解决了什么问题">解决了什么问题</a> ·
  <a href="#功能特性">功能特性</a> ·
  <a href="#技术栈">技术栈</a> ·
  <a href="#项目结构">项目结构</a> ·
  <a href="#截图">截图</a> ·
  <a href="#数据库">数据库</a> ·
  <a href="#默认账号">默认账号</a> ·
  <a href="#注意事项">注意事项</a>
</p>

<p align="center">
  <a href="README.md">English</a> · <a href="README.zh-CN.md"><strong>简体中文</strong></a>
</p>

---

<a id="安装运行"></a>
## 🚀 安装运行

### 前置条件
- **JDK 8 或更高**
- **MySQL 8.0 或更高**（脚本使用 `utf8mb4_0900_ai_ci`，需 MySQL 8.0.4+）
- **MySQL Connector/J 8.0.24** 驱动 jar（[下载](https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.24/mysql-connector-java-8.0.24.jar)）
- 一个带图形界面的环境（Swing 桌面程序）

### 步骤

**1. 获取代码**
```bash
git clone https://github.com/Je-taimais/Java-cinema-ticket-system.git
cd Java-cinema-ticket-system
```

**2. 准备数据库**

> ⚠️ SQL 脚本**不包含** `CREATE DATABASE` / `USE` 语句，请先手动创建名为 **`电影院`** 的数据库，再导入。

```sql
CREATE DATABASE `电影院` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `电影院`;
SOURCE database/cinema.sql;
```

**3. 配置数据库连接**

编辑 `src/cn/jbit/mbs/dao/JDBCUtil.java`：

```java
private static String url = "jdbc:mysql://localhost:3306/电影院";
private static String name = "root";
private static String password = "root";
```

如你的 MySQL 账号 / 密码 / 数据库名不同，请修改为对应值。

**4. 放置 JDBC 驱动**

将下载的驱动放到 `lib/`：
```
lib/mysql-connector-java-8.0.24.jar
```

**5. 编译**（在项目根目录执行）
```bash
javac -cp "lib/mysql-connector-java-8.0.24.jar" -d out $(find src -name "*.java")
```

> 使用 IntelliJ IDEA：直接打开本目录，把 `lib/mysql-connector-java-8.0.24.jar` 加入依赖（Libraries），运行主类 `cn.jbit.mbs.view.Login` 即可。

**6. 运行**（必须在项目根目录运行，以便加载 `picture/` 资源）
```bash
# Windows
java -cp "out;lib/mysql-connector-java-8.0.24.jar" cn.jbit.mbs.view.Login

# Linux / macOS
java -cp "out:lib/mysql-connector-java-8.0.24.jar" cn.jbit.mbs.view.Login
```

---

<a id="项目简介"></a>
## 🎬 项目简介

鱼眼电影是一套面向电影院的桌面端综合管理系统。它同时服务于两类用户：

- **普通观众**可以在图形界面中浏览影片、查看详情、按场次选座购票并在线支付、查看与管理个人订单；
- **影院管理员**可以在后台维护影片、影院、影厅、排片计划以及用户账号。

系统采用经典的分层结构（视图层 `view` + 数据访问层 `dao` + 实体层 `entity`），通过 JDBC 直连 MySQL，界面使用 Swing 手工搭建，无需任何 Web 容器或第三方框架，开箱即可在本地运行。

---

<a id="解决了什么问题"></a>
## 🎯 解决了什么问题

传统影院售票依赖窗口人工操作，存在排片混乱、余票不透明、统计困难等问题。本项目把“观众购票”与“影院运营”两条业务线整合进同一个桌面程序：

- **对观众**：提供可视化选片、场次选择、座位图选座、下单与支付（模拟支付宝 / 微信支付）的一体化体验；
- **对管理员**：提供影片上架/下架、影院与影厅管理、排片调度、用户管理的可视化后台，降低运营门槛；
- **对开发者**：是一个结构清晰、易于阅读的 Swing + JDBC 教学 / 课程设计示例。

---

<a id="功能特性"></a>
## ✨ 功能特性

### 观众端
- 用户注册、登录、找回密码
- 首页影片推荐与浏览
- 按类型（动作 / 科幻 / 剧情 …）筛选影片
- 影片详情（导演、演员、简介、评分、票房）
- 场次选择 + 座位图选座
- 下单与模拟支付（支付宝 / 微信）
- 我的订单管理（查看、退票）

### 管理端（`user_type = 2`）
- 影片管理：新增、删除、修改影片状态（未上映 / 上映中 / 已下映）
- 影院管理、影厅管理
- 排片管理（为影片安排场次与影厅、定价）
- 用户管理（查看、拉黑、启用/禁用）

---

<a id="技术栈"></a>
## 🧱 技术栈

| 层级 | 技术 |
| --- | --- |
| 语言 | Java 8+（Swing） |
| 界面 | Java Swing（手工编码，无 GUI 构建器） |
| 数据访问 | JDBC（原生 `PreparedStatement`） |
| 数据库 | MySQL 8.0+（由 MySQL 9.2 导出，使用 `utf8mb4_0900_ai_ci`） |
| 驱动 | MySQL Connector/J **8.0.24** |
| 架构 | 三层：`view`（界面） / `dao`（数据访问） / `entity`（实体） |

---

<a id="项目结构"></a>
## 📂 项目结构

```
cinema/
├── src/cn/jbit/mbs/
│   ├── view/          # Swing 界面层（登录、首页、详情、购票、管理后台…）
│   ├── dao/           # 数据访问接口与 JDBC 工具（JDBCUtil）
│   ├── dao/impl/      # 各实体 DAO 实现（movie/cinema/order/screening/user）
│   └── entity/        # 实体类（Movie, Cinema, Hall, Screening, Order, User…）
├── picture/           # 海报 / 背景 / 支付二维码等图片资源（约 43MB）
├── screenshots/       # 项目截图（本 README 使用）
├── database/
│   └── cinema.sql     # 数据库脚本（表结构与示例数据）
├── cinema.iml         # IntelliJ 模块文件
└── .gitignore
```

---

<a id="截图"></a>
## 🖼️ 截图

### 登录
![登录界面](screenshots/login.png)

### 首页
![首页](screenshots/home.png)

### 影片类型
![类型页面](screenshots/type.png)

### 电影详情
![电影详情](screenshots/movie-detail.png)

### 购票选座
![购票页面](screenshots/ticket-booking.png)

### 我的订单
![订单页面](screenshots/order.png)

### 管理员主界面
![管理员页面](screenshots/admin.png)

### 影院管理
![影院管理](screenshots/cinema-management.png)

### 影片管理
![影片管理](screenshots/movie-management.png)

---

<a id="数据库"></a>
## 🗄️ 数据库

数据库包含 7 张表：

| 表 | 说明 |
| --- | --- |
| `user` | 用户（普通用户 `user_type=0/1`、管理员 `=2`、拉黑 `=-1`） |
| `cinema` | 影院（名称、地址、营业状态） |
| `hall` | 影厅（所属影院、名称、状态） |
| `movie` | 影片（标题、导演、类型、评分、票房、状态、海报…） |
| `screening` | 排片（影片、影厅、起止时间、票价、状态） |
| `order` | 订单（订单号、用户、排片、金额、支付方式、状态） |
| `order_seat` | 订单座位（订单与具体座位的关联） |

---

<a id="默认账号"></a>
## 🔑 默认账号

> 密码在数据库中以明文存储（教学/课程设计用途，请勿用于生产环境）。

| 角色 | 用户名 | 密码 | 说明 |
| --- | --- | --- | --- |
| 管理员 | `小唐` | `123456` | `user_type = 2` |
| 管理员 | `小三` | `123456` | `user_type = 2` |
| 普通用户 | `小王` | `123456` | `user_type = 1` |
| 拉黑用户 | （演示用 `user_type = -1`） | — | 登录会被拒绝 |

---

<a id="注意事项"></a>
## ⚠️ 注意事项

- **数据库名含中文 `电影院`**：`JDBC` 连接串与建库名称必须完全一致，否则连不上。
- **默认账号密码**：`JDBCUtil` 中写死了 `root / root`，生产环境请务必修改。
- **`picture/` 资源**：约 43MB 的海报 / 背景 / 支付二维码图片，已随仓库提交以保证界面完整；运行时应从项目根目录启动。
- **支付为模拟**：支付宝 / 微信支付为界面模拟，不会真实扣款。

---

<a id="许可证"></a>
## 📄 许可证

本项目以 [MIT 许可证](LICENSE) 开源发布。你可以依据该许可证自由地使用、复制、修改与再分发，但须保留版权声明与许可声明。

---

<div align="center">Made with ❤️ by <a href="https://github.com/Je-taimais">Je-taimais</a> · <a href="README.md">English</a></div>
