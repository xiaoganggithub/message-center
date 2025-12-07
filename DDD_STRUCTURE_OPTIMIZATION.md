# DDD结构优化方案

## 当前问题

1. **重复的repository** - 根目录和domain下都有repository
2. **重复的service** - 根目录下有旧的service实现
3. **空文件夹** - adapter, config, controller, executor, handler, mapper, strategy
4. **domain/service实现分离** - impl子包应该合并

## 优化后的标准DDD结构

```
src/main/java/com/message/center/
├── application/                    # 应用层
│   ├── command/                    # 命令（暂时为空，预留）
│   ├── query/                      # 查询（暂时为空，预留）
│   ├── handler/                    # 责任链处理器
│   ├── service/                    # 应用服务
│   └── strategy/                   # 执行策略
├── domain/                         # 领域层
│   ├── entity/                     # 实体（聚合根）
│   ├── valueobject/                # 值对象（删除vo文件夹，统一到这里）
│   ├── enums/                      # 枚举
│   ├── event/                      # 领域事件（预留）
│   ├── repository/                 # 仓储接口
│   └── service/                    # 领域服务（合并impl）
├── infrastructure/                 # 基础设施层
│   ├── adapter/                    # 外部系统适配器
│   ├── config/                     # 配置
│   ├── executor/                   # 任务执行器
│   └── data/                       # 数据访问
│       ├── mapper/                 # MyBatis Mapper
│       └── repository/             # 仓储实现
├── interfaces/                     # 接口层
│   ├── rest/                       # REST控制器
│   └── dubbo/                      # Dubbo服务
│       ├── api/                    # Dubbo接口定义
│       └── impl/                   # Dubbo接口实现
└── MessageCenterApplication.java   # 应用入口
```

## 需要删除的文件夹

- `src/main/java/com/message/center/repository/` (旧的实现，已迁移到infrastructure)
- `src/main/java/com/message/center/service/` (旧的实现，已迁移到application)
- `src/main/java/com/message/center/adapter/` (空文件夹)
- `src/main/java/com/message/center/config/` (空文件夹)
- `src/main/java/com/message/center/controller/` (空文件夹)
- `src/main/java/com/message/center/executor/` (空文件夹)
- `src/main/java/com/message/center/handler/` (空文件夹)
- `src/main/java/com/message/center/mapper/` (空文件夹)
- `src/main/java/com/message/center/strategy/` (空文件夹)

## 需要重命名的文件夹

- `domain/vo/` → 保留（这些是值对象，符合DDD规范）
- `domain/valueobject/` → 删除（空文件夹）
- `domain/service/impl/` → 合并到 `domain/service/`

## 优化步骤

1. 删除旧的repository和service文件夹
2. 删除所有空文件夹
3. 合并domain/service的实现类
4. 验证编译通过
