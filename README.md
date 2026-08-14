# 鱼眼电影 · FishEye Movie

> 一个基于 **Java Swing + JDBC + MySQL** 的桌面端影院售票与管理系统。
> A desktop **cinema ticket-booking & management system** built with **Java Swing + JDBC + MySQL**.

---

## 📌 项目简介 / Project Introduction

**中文：** 鱼眼电影是一套面向电影院的桌面端综合管理系统。它同时服务于两类用户：普通观众可以在图形界面中浏览影片、查看详情、按场次选座购票并在线支付、查看与管理个人订单；影院管理员可以在后台维护影片、影院、影厅、排片计划以及用户账号。系统采用经典的分层结构（视图层 `view` + 数据访问层 `dao` + 实体层 `entity`），通过 JDBC 直连 MySQL，界面使用 Swing 手工搭建，无需任何 Web 容器或第三方框架，开箱即可在本地运行。

**English:** FishEye Movie is a comprehensive desktop management system for cinemas. It serves two roles: moviegoers can browse films, view details, pick seats by session, pay online, and manage their own orders through a GUI; cinema administrators can maintain movies, cinemas, halls, scheduling plans, and user accounts from a backend console. The project follows a classic layered architecture (`view` + `dao` + `entity`), connects to MySQL directly via JDBC, and builds its UI with hand-coded Swing — no web server or third-party framework required, so it runs locally out of the box.

---

## 🎯 解决了什么问题 / Problem It Solves

**中文：** 传统影院售票依赖窗口人工操作，存在排片混乱、余票不透明、统计困难等问题。本项目把"观众购票"与"影院运营"两条业务线整合进同一个桌面程序：

- 对观众：提供可视化选片、场次选择、座位图选座、下单与支付（模拟支付宝 / 微信支付）的一体化体验；
- 对管理员：提供影片上架/下架、影院与影厅管理、排片调度、用户管理的可视化后台，降低运营门槛；
- 对开发者：是一个结构清晰、易于阅读的 Swing + JDBC 教学 / 课程设计示例。

**English:** Traditional cinema ticketing relies on manual counter operations, which leads to messy scheduling, opaque seat availability, and hard-to-track statistics. This project unifies "audience ticketing" and "cinema operations" into one desktop program:

- For audiences: a one-stop experience of browsing, session selection, seat-map picking, ordering, and payment (simulated Alipay / WeChat Pay).
- For administrators: a visual backend for movie on/off-lining, cinema & hall management, scheduling, and user management — lowering the operations barrier.
- For developers: a clean, readable Swing + JDBC example suitable for coursework / learning.

---

## ✨ 功能特性 / Features

### 观众端 / Audience
- 用户注册、登录、找回密码 / Register, login, password recovery
- 首页影片推荐与浏览 / Home page recommendations & browsing
- 按类型（动作 / 科幻 / 剧情 …）筛选影片 / Filter movies by genre
- 影片详情（导演、演员、简介、评分、票房）/ Movie detail (director, cast, synopsis, rating, box office)
- 场次选择 + 座位图选座 / Session selection + seat-map picking
- 下单与模拟支付（支付宝 / 微信）/ Order placement & simulated payment
- 我的订单管理（查看、退票）/ My orders (view, refund)

### 管理端 / Administrator (`user_type = 2`)
- 影片管理：新增、删除、修改影片状态（未上映 / 上映中 / 已下映）/ Movie management
- 影院管理、影厅管理 / Cinema & hall management
- 排片管理（为影片安排场次与影厅、定价）/ Scheduling management
- 用户管理（查看、拉黑、启用/禁用）/ User management (blacklist, enable/disable)

---

## 🧱 技术栈 / Tech Stack

| 类别 / Layer | 技术 / Technology |
| --- | --- |
| 语言 / Language | Java 8+ (Swing) |
| 界面 / UI | Java Swing (hand-coded, no GUI builder) |
| 数据访问 / Data | JDBC (原生 `PreparedStatement`) |
| 数据库 / Database | MySQL 8.0+ (dump from MySQL 9.2, uses `utf8mb4_0900_ai_ci`) |
| 驱动 / Driver | MySQL Connector/J **8.0.24** |
| 架构 / Architecture | 三层：`view`(界面) / `dao`(数据访问) / `entity`(实体) |

---

## 📂 项目结构 / Project Structure

```
cinema/
├── src/cn/jbit/mbs/
│   ├── view/          # Swing 界面层 (登录、首页、详情、购票、管理后台…)
│   ├── dao/           # 数据访问接口与 JDBC 工具 (JDBCUtil)
│   ├── dao/impl/      # 各实体 DAO 实现 (movie/cinema/order/screening/user)
│   └── entity/        # 实体类 (Movie, Cinema, Hall, Screening, Order, User…)
├── picture/           # 海报 / 背景 / 支付二维码等图片资源 (约 43MB)
├── screenshots/       # 项目截图 (本 README 使用)
├── database/
│   └── cinema.sql     # 数据库脚本 (表结构与示例数据)
├── cinema.iml         # IntelliJ 模块文件
└── .gitignore
```

---

## 🖼️ 截图 / Screenshots

### 登录 / Login
![登录界面 Login](screenshots/login.png)

### 首页 / Home
![首页 Home](screenshots/home.png)

### 影片类型 / Movie Categories
![类型页面 Categories](screenshots/type.png)

### 电影详情 / Movie Detail
![电影详情 Movie Detail](screenshots/movie-detail.png)

### 购票选座 / Ticket Booking
![购票页面 Ticket Booking](screenshots/ticket-booking.png)

### 我的订单 / My Orders
![订单页面 Orders](screenshots/order.png)

### 管理员主界面 / Admin Dashboard
![管理员页面 Admin](screenshots/admin.png)

### 影院管理 / Cinema Management
![影院管理 Cinema Management](screenshots/cinema-management.png)

### 影片管理 / Movie Management
![影片管理 Movie Management](screenshots/movie-management.png)

---

## 🗄️ 数据库 / Database

数据库包含 7 张表 / The database contains 7 tables:

| 表 / Table | 说明 / Description |
| --- | --- |
| `user` | 用户（普通用户 `user_type=0/1`、管理员 `=2`、拉黑 `=-1`） |
| `cinema` | 影院（名称、地址、营业状态） |
| `hall` | 影厅（所属影院、名称、状态） |
| `movie` | 影片（标题、导演、类型、评分、票房、状态、海报…） |
| `screening` | 排片（影片、影厅、起止时间、票价、状态） |
| `order` | 订单（订单号、用户、排片、金额、支付方式、状态） |
| `order_seat` | 订单座位（订单与具体座位的关联） |

---

## ⚙️ 安装与运行 / Installation & Running

### 前置条件 / Prerequisites
- **JDK 8 或更高** / JDK 8 or newer
- **MySQL 8.0 或更高**（脚本使用 `utf8mb4_0900_ai_ci`，需 MySQL 8.0.4+）/ MySQL 8.0+
- **MySQL Connector/J 8.0.24** 驱动 jar（[Maven Central 下载](https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.24/mysql-connector-java-8.0.24.jar)）
- 一个带图形界面的环境（Swing 桌面程序）/ A graphical environment (Swing desktop app)

### 步骤 / Steps

**1. 获取代码 / Get the code**
```bash
git clone https://github.com/Je-taimais/Java-cinema-ticket-system.git
cd Java-cinema-ticket-system
```

**2. 准备数据库 / Prepare the database**

> ⚠️ SQL 脚本**不包含** `CREATE DATABASE` / `USE` 语句，请先手动创建名为 **`电影院`** 的数据库（名称含中文，必须保持一致），再导入。
> The SQL file has **no** `CREATE DATABASE`/`USE`. First create a database literally named **`电影院`**, then import.

```sql
CREATE DATABASE `电影院` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `电影院`;
SOURCE database/cinema.sql;
```

（也可在 MySQL 客户端 / Navicat 中执行 `database/cinema.sql` 前先执行上面的建库语句。）
(You can also run the two statements above in any MySQL client / Navicat, then import `database/cinema.sql`.)

**3. 配置数据库连接 / Configure DB connection**

编辑 / Edit `src/cn/jbit/mbs/dao/JDBCUtil.java`：

```java
private static String url = "jdbc:mysql://localhost:3306/电影院";
private static String name = "root";
private static String password = "root";
```

如你的 MySQL 账号 / 密码 / 数据库名不同，请修改为对应值。
Change these to match your own MySQL username / password / database name if different.

**4. 放置 JDBC 驱动 / Add the JDBC driver**

将下载的驱动放到 / Put the downloaded jar into `lib/`：

```
lib/mysql-connector-java-8.0.24.jar
```

**5. 编译 / Compile**  （在项目根目录执行 / Run from project root）

```bash
# Windows (Git Bash) / Linux / macOS
javac -cp "lib/mysql-connector-java-8.0.24.jar" -d out $(find src -name "*.java")
```

> 使用 IntelliJ IDEA：直接打开本目录，把 `lib/mysql-connector-java-8.0.24.jar` 加入依赖（Libraries），运行主类 `cn.jbit.mbs.view.Login` 即可。
> With IntelliJ IDEA: open the folder, add the jar to Libraries, and run `cn.jbit.mbs.view.Login`.

**6. 运行 / Run**  （必须在项目根目录运行，以便加载 `picture/` 资源 / Must run from root so `picture/` resources load）

```bash
# Windows 用分号 ";"
java -cp "out;lib/mysql-connector-java-8.0.24.jar" cn.jbit.mbs.view.Login

# Linux / macOS 用冒号 ":"
java -cp "out:lib/mysql-connector-java-8.0.24.jar" cn.jbit.mbs.view.Login
```

---

## 🔑 默认账号 / Default Accounts

> 密码在数据库中以明文存储（教学/课程设计用途，请勿用于生产环境）。
> Passwords are stored in plaintext (for learning / coursework only — do not use in production).

| 角色 / Role | 用户名 / Username | 密码 / Password | 说明 / Note |
| --- | --- | --- | --- |
| 管理员 / Admin | `小唐` | `123456` | `user_type = 2` |
| 管理员 / Admin | `小三` | `123456` | `user_type = 2` |
| 普通用户 / User | `小王` | `123456` | `user_type = 1` |
| 拉黑用户 / Banned | （演示用 `user_type = -1`） | — | 登录会被拒绝 |

---

## ⚠️ 注意事项 / Notes

- **数据库名含中文 `电影院`**：JDBC 连接串与建库名称必须完全一致，否则连不上。
  The DB name `电影院` is Chinese — keep it exactly identical in both the `CREATE DATABASE` and the JDBC URL.
- **默认账号密码**：`JDBCUtil` 中写死了 `root / root`，生产环境请务必修改。
  `JDBCUtil` hardcodes `root / root`; change it for any real deployment.
- **`picture/` 资源**：约 43MB 的海报 / 背景 / 支付二维码图片，已随仓库提交以保证界面完整；运行时应从项目根目录启动。
  The `picture/` folder (~43MB of posters / backgrounds / QR codes) is committed so the UI is complete; run from the project root.
- **`default_avatar.png` 缺失**：代码默认头像文件未包含在资源中，缺失时头像显示为空，不影响功能。
  `default_avatar.png` is not shipped; the avatar just appears empty — functionality is unaffected.
- **支付为模拟**：支付宝 / 微信支付为界面模拟，不会真实扣款。
  Alipay / WeChat payment is simulated and does not charge real money.

---

## 📄 许可证 / License

本项目为教学 / 课程设计示例，未包含独立许可证文件；如需用于其他用途请自行添加合适许可证。
This is a learning / coursework demo with no dedicated license file; add an appropriate license before reuse.

---

Made with Java Swing & JDBC · 鱼眼电影 FishEye Movie
