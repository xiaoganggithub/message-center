# 推送到GitHub步骤

## 1. 在GitHub上创建新仓库

访问 https://github.com/new 创建一个新仓库，例如命名为 `message-center`

## 2. 添加远程仓库地址

```bash
git remote add origin https://github.com/你的用户名/message-center.git
```

或者使用SSH:

```bash
git remote add origin git@github.com:你的用户名/message-center.git
```

## 3. 推送代码到GitHub

```bash
# 推送到main分支
git branch -M main
git push -u origin main
```

## 4. 验证

访问你的GitHub仓库页面，确认代码已成功推送。

---

## 已完成的优化

✅ 删除根目录下重复的repository和service文件夹
✅ 删除空的文件夹(adapter, config, controller, executor, handler, mapper, strategy)
✅ 合并domain/service的实现类到同一层
✅ 删除domain/valueobject空文件夹
✅ 更新README添加DDD架构说明
✅ 添加.gitignore文件
✅ 提交到本地Git仓库

## 优化后的DDD四层架构

```
src/main/java/com/message/center/
├── interfaces/          # 接口层(REST/Dubbo)
├── application/         # 应用层(handler/service/strategy)
├── domain/             # 领域层(entity/vo/enums/service/repository)
└── infrastructure/     # 基础设施层(adapter/config/executor/data)
```
