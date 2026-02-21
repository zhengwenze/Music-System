# Mod_music 音乐模块接口测试文档

## 1. 推荐歌曲接口

### 接口信息
- **URL**: `/music/Recommend`
- **方法**: `GET`
- **功能**: 获取随机推荐的10首歌曲

### 请求参数
无

### 请求示例
```http
GET /music/Recommend
```

### 响应数据
```json
{
  "code": 200,
  "message": "获取推荐歌曲成功",
  "data": {
    "list": [
      {
        "musicId": 1,
        "musicName": "示例歌曲1",
        "singer": "歌手A",
        "img": "/default.jpg",
        "url": "/music/1.mp3",
        "listenNumb": 100,
        "createTime": "2025-01-01 00:00:00",
        "isLike": 0
      },
      // 更多歌曲...
    ]
  }
}
```

## 2. 音乐排行榜接口

### 接口信息
- **URL**: `/music/RecommendList`
- **方法**: `GET`
- **功能**: 获取音乐排行榜

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| param | String | 是 | 排行类型：'hot'(热门) 或 'newest'(最新) |

### 请求示例
```http
GET /music/RecommendList?param=hot
```

### 响应数据
```json
{
  "code": 200,
  "message": "获取排行榜成功",
  "data": {
    "list": [
      {
        "musicId": 1,
        "musicName": "热门歌曲1",
        "singer": "歌手A",
        "img": "/default.jpg",
        "url": "/music/1.mp3",
        "listenNumb": 1000,
        "createTime": "2025-01-01 00:00:00",
        "isLike": 0
      },
      // 更多歌曲...
    ]
  }
}
```

## 3. 搜索歌曲接口

### 接口信息
- **URL**: `/music/search`
- **方法**: `GET`
- **功能**: 根据关键词搜索歌曲

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| keyword | String | 是 | 搜索关键词 |

### 请求示例
```http
GET /music/search?keyword=爱情
```

### 响应数据
```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "list": [
      {
        "musicId": 1,
        "musicName": "爱情转移",
        "singer": "陈奕迅",
        "img": "/default.jpg",
        "url": "/music/1.mp3",
        "listenNumb": 500,
        "createTime": "2025-01-01 00:00:00",
        "isLike": 0
      },
      // 更多歌曲...
    ],
    "total": 2
  }
}
```

## 4. 歌曲详情接口

### 接口信息
- **URL**: `/music/detail`
- **方法**: `GET`
- **功能**: 获取歌曲详细信息

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| musicId | Integer | 是 | 歌曲ID（查询参数） |

### 请求头
| 头字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| id | Integer | 否 | 用户ID，用于判断是否已点赞 |

### 请求示例
```http
GET /music/detail?musicId=1
id: 2
```

### 响应数据
```json
{
  "code": 200,
  "message": "获取歌曲详情成功",
  "data": {
    "music": {
      "musicId": 1,
      "musicName": "示例歌曲",
      "singer": "歌手A",
      "img": "/default.jpg",
      "url": "/music/1.mp3",
      "listenNumb": 101,
      "createTime": "2025-01-01 00:00:00",
      "isLike": 1,
      "fromSinger": 3,
      "content": "歌词内容...",
      "album": "专辑名称"
    }
  }
}
```

## 5. 歌手推荐接口

### 接口信息
- **URL**: `/music/RecommendSinger`
- **方法**: `GET`
- **功能**: 获取推荐歌手列表

### 请求参数
无

### 请求示例
```http
GET /music/RecommendSinger
```

### 响应数据
```json
{
  "code": 200,
  "message": "获取推荐歌手成功",
  "data": {
    "singers": [
      {
        "id": 1,
        "username": "默认歌手",
        "avatar": "/default_avatar.jpg",
        "about": "歌手简介"
      },
      // 更多歌手...
    ]
  }
}
```

## 6. 歌手详情接口

### 接口信息
- **URL**: `/music/singerDetail`
- **方法**: `GET`
- **功能**: 获取歌手详细信息和其歌曲列表

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| id | Integer | 是 | 歌手ID |

### 请求示例
```http
GET /music/singerDetail?id=1
```

### 响应数据
```json
{
  "code": 200,
  "message": "获取歌手详情成功",
  "data": {
    "singer": {
      "id": 1,
      "username": "歌手 1",
      "avatar": "/default_avatar.jpg",
      "about": "歌手简介"
    },
    "musicList": [
      {
        "musicId": 1,
        "musicName": "歌手的歌曲1",
        "singer": "歌手 1",
        "img": "/default.jpg",
        "url": "/music/1.mp3",
        "listenNumb": 100,
        "createTime": "2025-01-01 00:00:00",
        "isLike": 0
      },
      // 更多歌曲...
    ]
  }
}
```

## 7. 最新歌曲推荐接口

### 接口信息
- **URL**: `/music/RecommendNewest`
- **方法**: `GET`
- **功能**: 获取最新上传的歌曲列表

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| size | Integer | 否 | 返回数量，默认10，最大100 |

### 请求示例
```http
GET /music/RecommendNewest?size=5
```

### 响应数据
```json
{
  "code": 200,
  "message": "获取最新歌曲成功",
  "data": {
    "list": [
      {
        "musicId": 1,
        "musicName": "最新歌曲1",
        "singer": "歌手A",
        "img": "/default.jpg",
        "url": "/music/1.mp3",
        "listenNumb": 10,
        "createTime": "2025-01-10 00:00:00",
        "isLike": 0
      },
      // 更多歌曲...
    ],
    "size": 5
  }
}
```

## 实体类说明

### Music实体类字段说明
| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| musicId | Integer | 歌曲ID |
| musicName | String | 歌曲名称 |
| singer | String | 歌手名称 |
| img | String | 歌曲封面图片URL |
| url | String | 歌曲文件URL |
| listenNumb | Integer | 播放次数 |
| createTime | String | 创建时间 |
| isLike | Integer | 是否已点赞(0-未点赞, 1-已点赞) |
| fromSinger | Integer | 所属歌手ID |
| content | String | 歌词内容 |
| album | String | 专辑名称 |

### User实体类字段说明
| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| id | Integer | 用户ID |
| username | String | 用户名/歌手名称 |
| avatar | String | 头像URL |
| about | String | 个人简介 |
| role | String | 用户角色 |
| activation | Integer | 激活状态 |

### Mess响应对象字段说明
| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| code | Integer | 响应状态码 |
| message | String | 响应消息 |
| data | Object | 响应数据 |