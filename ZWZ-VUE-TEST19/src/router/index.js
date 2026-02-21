import { createRouter, createWebHistory } from "vue-router";
import Layout from "../layout/index.vue";
import { ElMessage } from "element-plus";
import { getUserId, getUserRole, clearUserInfo } from "@/utils/auth"; // 修改：使用getUserId替代getToken

const routes = [
  {
    path: "/",
    redirect: "/login",
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/login/index.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("../views/register/index.vue"),
    meta: { title: "注册" },
  },
  {
    path: "/user",
    component: Layout,
    // 重定向到热门推荐页面hotMusic
    redirect: "/user/hotMusic",
    meta: { title: "用户端", role: "user" },
    children: [
      {
        path: "profile",
        name: "Profile",
        component: () => import("../views/user/profile/Index.vue"),
        meta: { title: "个人信息" },
      },
      {
        path: "hotMusic",
        name: "HotMusic",
        component: () => import("../views/user/hotMusic/Index.vue"),
        meta: { title: "热门推荐" },
      },
      {
        path: "ranking",
        name: "UserRanking",
        component: () => import("../views/user/ranking/Index.vue"),
        meta: { title: "歌曲排行" },
      },
      {
        path: "featuredSinger",
        name: "FeaturedSinger",
        component: () => import("../views/user/featuredSinger/Index.vue"),
        meta: { title: "精选歌手" },
      },
      {
        path: "mySongList",
        name: "UserMySongList",
        component: () => import("../views/user/mySongList/Index.vue"),
        meta: { title: "我的歌单" },
      },
      {
        path: "likePlaylist",
        name: "LikePlaylist",
        component: () => import("../views/user/likePlaylist/Index.vue"),
        meta: { title: "收藏歌单" },
      },
      {
        path: "likeMusic",
        name: "LikeMusic",
        component: () => import("../views/user/likeMusic/Index.vue"),
        meta: { title: "收藏音乐" },
      },
      {
        path: "message",
        name: "UserMessage",
        component: () => import("../views/user/message/Index.vue"),
        meta: { title: "消息通知" },
      },
    ],
  },
  {
    path: "/singer",
    component: Layout,
    // 重定向到上传音乐界面uploadMusic
    redirect: "/singer/uploadMusic",
    meta: { title: "歌手端", role: "singer" },
    children: [
      {
        path: "profile",
        name: "SingerProfile",
        component: () => import("../views/singer/profile/Index.vue"),
        meta: { title: "个人信息" },
      },
      {
        path: "uploadMusic",
        name: "UploadMusic",
        component: () => import("../views/singer/uploadMusic/Index.vue"),
        meta: { title: "上传音乐" },
      },
      {
        path: "musicManage",
        name: "SingerMusicManage",
        component: () => import("../views/singer/musicManage/Index.vue"),
        meta: { title: "歌曲管理", role: "singer" },
      },
      {
        path: "hotMusic",
        name: "SingerHotMusic",
        component: () => import("../views/singer/hotMusic/Index.vue"),
        meta: { title: "热门推荐" },
      },
      {
        path: "musicManage",
        name: "SingerMusicManage",
        component: () => import("../views/singer/musicManage/Index.vue"),
        meta: { title: "歌曲管理", role: "singer" },
      },
      {
        path: "ranking",
        name: "SingerRanking",
        component: () => import("../views/singer/ranking/Index.vue"),
        meta: { title: "歌曲排行" },
      },
      {
        path: "mySongList",
        name: "SingMySongList",
        component: () => import("../views/singer/mySongList/Index.vue"),
        meta: { title: "我的歌单" },
      },
      {
        path: "likeSongList",
        name: "SingerLikeSongList",
        component: () => import("../views/singer/likeSongList/Index.vue"),
        meta: { title: "收藏歌单" },
      },
      {
        path: "likeMusic",
        name: "SingerLikeMusic",
        component: () => import("../views/singer/likeMusic/Index.vue"),
        meta: { title: "收藏音乐" },
      },
      {
        path: "message",
        name: "SingerMessage",
        component: () => import("../views/singer/message/Index.vue"),
        meta: { title: "消息通知" },
      },
      {
        path: "detail",
        name: "SingerDetail",
        component: () => import("../views/singer/detail/Index.vue"),
        meta: { title: "歌手详情" },
      },
    ],
  },
  // 管理员端路由
  {
    path: "/admin",
    component: Layout,
    // 重定向到用户管理页面userManage
    redirect: "/admin/userManage",
    meta: { title: "管理员端", role: "admin" },
    children: [
      {
        path: "profile",
        name: "AdminProfile",
        component: () => import("../views/admin/profile/Index.vue"),
        meta: { title: "个人信息" },
      },
      {
        path: "userManage",
        name: "UserManage",
        component: () => import("../views/admin/userManage/Index.vue"),
        meta: { title: "用户管理" },
      },
      {
        path: "singerManage",
        name: "SingerManage",
        component: () => import("../views/admin/singerManage/Index.vue"),
        meta: { title: "歌手管理" },
      },
      {
        path: "musicManage",
        name: "MusicManage",
        component: () => import("../views/admin/musicManage/Index.vue"),
        meta: { title: "音乐管理" },
      },
      {
        path: "message",
        name: "AdminMessage",
        component: () => import("../views/admin/message/Index.vue"),
        meta: { title: "消息中心" },
      },
    ],
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: () => import("../views/error/404.vue"),
    meta: { title: "页面不存在" },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  },
});

router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title
    ? `${to.meta.title} - 悦享音乐`
    : "悦享音乐";

  // 不需要登录的页面
  const publicPages = ["/login", "/register"];
  const authRequired = !publicPages.includes(to.path);

  // 获取用户信息 - 修改：使用userId替代token
  const userId = getUserId();
  const userRole = getUserRole();

  console.log(`路由切换: ${from.path} -> ${to.path}, userId: ${userId ? '存在' : '不存在'}, 角色: ${userRole || '未知'}`);

  // 修改：使用userId验证登录状态
  if (authRequired && !userId) {
    ElMessage.warning("请先登录");
    return next("/login");
  }

  // 检查路由权限 - 修改：角色映射逻辑
  if (authRequired && to.meta.role) {
    // 角色映射：数字编码到角色名称
    const roleMap = {
      0: "admin",
      1: "singer",
      2: "user"
    };

    const userRoleName = roleMap[userRole] || userRole;

    if (to.meta.role !== userRoleName) {
      // 如果用户没有访问该路由的权限，根据角色重定向到对应的首页
      if (userRole === 0) {
        return next("/admin/profile");
      } else if (userRole === 1) {
        return next("/singer/profile");
      } else if (userRole === 2) {
        return next("/user/profile");
      } else {
        // 角色不明确，退出登录
        clearUserInfo();
        ElMessage.error("权限不足，请重新登录");
        return next("/login");
      }
    }
  }

  // 修改：使用userId判断登录状态
  if (userId && (to.path === "/login" || to.path === "/register")) {
    if (userRole === 0) {
      return next("/admin/profile");
    } else if (userRole === 1) {
      return next("/singer/profile");
    } else if (userRole === 2) {
      return next("/user/profile");
    }
  }

  // 修改：使用userId判断登录状态
  if (userId && to.path === "/") {
    if (userRole === 0) {
      return next("/admin/profile");
    } else if (userRole === 1) {
      return next("/singer/profile");
    } else if (userRole === 2) {
      return next("/user/profile");
    }
  }

  next();
});

export default router;