import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/:pathMatch(.*)*',
        name: 'Dashboard',
        component: () => import("@/components/organism/DashHero.vue")
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

function hasQueryParams (route) {
    return !!Object.keys(route.query).length
}

router.beforeEach((to, from, next) => {
    if (!hasQueryParams(to) && hasQueryParams(from)) {
        next({ name: to.name, query: from.query })
    } else {
        next()
    }
})

export default router