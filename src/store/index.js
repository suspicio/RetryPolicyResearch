import { createStore } from 'vuex'

const store = createStore({
    state: {
        profile: {}
    },

    mutations: {
        setProfile(state, newProfile) {
            localStorage.setItem('activeProfile', JSON.stringify(newProfile))
            state.profile = newProfile
        },
    },

    actions: {
        saveProfile (state, data) {
            state.state.profile = data;
            localStorage.setItem('activeProfile', JSON.stringify(data))
            localStorage.setItem(data.email, JSON.stringify(data));
        },

        getProfile (state) {
            if (localStorage.getItem('activeProfile') === null)
                return null
            state.state.profile = JSON.parse(localStorage.getItem('activeProfile'));
            return state.state.profile;
        }
    },

    getters: {
    }
})

export default store;