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
    component: () => import(/* webpackChunkName: "login" */ "../views/login/index.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import(/* webpackChunkName: "register" */ "../views/register/index.vue"),
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
        component: () => import(/* webpackChunkName: "user-profile" */ "../views/user/profile/Index.vue"),
        meta: { title: "个人信息" },
      },
      {
        path: "hotMusic",
        name: "HotMusic",
        component: () => import(/* webpackChunkName: "user-hotmusic" */ "../views/user/hotMusic/Index.vue"),
        meta: { title: "热门推荐" },
      },
      {
        path: "ranking",
        name: "UserRanking",
        component: () => import(/* webpackChunkName: "user-ranking" */ "../views/user/ranking/Index.vue"),
        meta: { title: "歌曲排行" },
      },
      {
        path: "featuredSinger",
        name: "FeaturedSinger",
        component: () => import(/* webpackChunkName: "user-featuredsinger" */ "../views/user/featuredSinger/Index.vue"),
        meta: { title: "精选歌手" },
      },
      {
        path: "mySongList",
        name: "UserMySongList",
        component: () => import(/* webpackChunkName: "user-mysonglist" */ "../views/user/mySongList/Index.vue"),
        meta: { title: "我的歌单" },
      },
      {
        path: "likePlaylist",
        name: "LikePlaylist",
        component: () => import(/* webpackChunkName: "user-likeplaylist" */ "../views/user/likePlaylist/Index.vue"),
        meta: { title: "收藏歌单" },
      },
      {
        path: "likeMusic",
        name: "LikeMusic",
        component: () => import(/* webpackChunkName: "user-likemusic" */ "../views/user/likeMusic/Index.vue"),
        meta: { title: "收藏音乐" },
      },
      {
        path: "message",
        name: "UserMessage",
        component: () => import(/* webpackChunkName: "user-message" */ "../views/user/message/Index.vue"),
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
        component: () => import(/* webpackChunkName: "singer-profile" */ "../views/singer/profile/Index.vue"),
        meta: { title: "个人信息" },
      },
      {
        path: "uploadMusic",
        name: "UploadMusic",
        component: () => import(/* webpackChunkName: "singer-uploadmusic" */ "../views/singer/uploadMusic/Index.vue"),
        meta: { title: "上传音乐" },
      },
      {
        path: "musicManage",
        name: "SingerMusicManage",
        component: () => import(/* webpackChunkName: "singer-musicmanage" */ "../views/singer/musicManage/Index.vue"),
        meta: { title: "歌曲管理", role: "singer" },
      },
      {
        path: "hotMusic",
        name: "SingerHotMusic",
        component: () => import(/* webpackChunkName: "singer-hotmusic" */ "../views/singer/hotMusic/Index.vue"),
        meta: { title: "热门推荐" },
      },
      {
        path: "ranking",
        name: "SingerRanking",
        component: () => import(/* webpackChunkName: "singer-ranking" */ "../views/singer/ranking/Index.vue"),
        meta: { title: "歌曲排行" },
      },
      {
        path: "mySongList",
        name: "SingMySongList",
        component: () => import(/* webpackChunkName: "singer-mysonglist" */ "../views/singer/mySongList/Index.vue"),
        meta: { title: "我的歌单" },
      },
      {
        path: "likeSongList",
        name: "SingerLikeSongList",
        component: () => import(/* webpackChunkName: "singer-likesonglist" */ "../views/singer/likeSongList/Index.vue"),
        meta: { title: "收藏歌单" },
      },
      {
        path: "likeMusic",
        name: "SingerLikeMusic",
        component: () => import(/* webpackChunkName: "singer-likemusic" */ "../views/singer/likeMusic/Index.vue"),
        meta: { title: "收藏音乐" },
      },
      {
        path: "message",
        name: "SingerMessage",
        component: () => import(/* webpackChunkName: "singer-message" */ "../views/singer/message/Index.vue"),
        meta: { title: "消息通知" },
      },
      {
        path: "detail",
        name: "SingerDetail",
        component: () => import(/* webpackChunkName: "singer-detail" */ "../views/singer/detail/Index.vue"),
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
        component: () => import(/* webpackChunkName: "admin-profile" */ "../views/admin/profile/Index.vue"),
        meta: { title: "个人信息" },
      },
      {
        path: "userManage",
        name: "UserManage",
        component: () => import(/* webpackChunkName: "admin-usermanage" */ "../views/admin/userManage/Index.vue"),
        meta: { title: "用户管理" },
      },
      {
        path: "singerManage",
        name: "SingerManage",
        component: () => import(/* webpackChunkName: "admin-singermanage" */ "../views/admin/singerManage/Index.vue"),
        meta: { title: "歌手管理" },
      },
      {
        path: "musicManage",
        name: "MusicManage",
        component: () => import(/* webpackChunkName: "admin-musicmanage" */ "../views/admin/musicManage/Index.vue"),
        meta: { title: "音乐管理" },
      },
      {
        path: "message",
        name: "AdminMessage",
        component: () => import(/* webpackChunkName: "admin-message" */ "../views/admin/message/Index.vue"),
        meta: { title: "消息中心" },
      },
    ],
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: () => import(/* webpackChunkName: "error-404" */ "../views/error/404.vue"),
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
  document.title = to.meta.title
    ? `${to.meta.title} - 悦享音乐`
    : "悦享音乐";

  const publicPages = ["/login", "/register"];
  const authRequired = !publicPages.includes(to.path);

  const userId = getUserId();
  const userRole = getUserRole();

  console.log(`路由切换: ${from.path} -> ${to.path}, userId: ${userId ? '存在' : '不存在'}, 角色: ${userRole}`);

  if (authRequired && !userId) {
    ElMessage.warning("请先登录");
    next("/login");
    return;
  }

  const roleMap = {
    0: "admin",
    1: "singer",
    2: "user"
  };
  const userRoleName = roleMap[userRole];

  const pathRoleMap = {
    "/admin": "admin",
    "/user": "user",
    "/singer": "singer"
  };
  
  let requiredRole = null;
  for (const [prefix, role] of Object.entries(pathRoleMap)) {
    if (to.path.startsWith(prefix + "/") || to.path === prefix) {
      requiredRole = role;
      break;
    }
  }

  if (authRequired && requiredRole && requiredRole !== userRoleName) {
    console.log(`权限不足: 需要 ${requiredRole}, 当前 ${userRoleName}`);
    if (userRole === 0) {
      next("/admin/profile");
    } else if (userRole === 1) {
      next("/singer/profile");
    } else if (userRole === 2) {
      next("/user/profile");
    } else {
      clearUserInfo();
      ElMessage.error("权限不足，请重新登录");
      next("/login");
    }
    return;
  }

  if (userId && (to.path === "/login" || to.path === "/register")) {
    if (userRole === 0) {
      next("/admin/profile");
    } else if (userRole === 1) {
      next("/singer/profile");
    } else if (userRole === 2) {
      next("/user/profile");
    } else {
      next();
    }
    return;
  }

  if (userId && to.path === "/") {
    if (userRole === 0) {
      next("/admin/profile");
    } else if (userRole === 1) {
      next("/singer/profile");
    } else if (userRole === 2) {
      next("/user/profile");
    } else {
      next();
    }
    return;
  }

  next();
});

export default router;