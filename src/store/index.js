import { createStore } from 'vuex'
import axios from "axios";

const store = createStore({
    state: {
        profile: {},
        profilesIds: [],
        configs: [],
        selectedConfig: {}
    },

    mutations: {
        appendConfigs(state, newConfig) {
            state.configs.push(newConfig);
        },

        deleteConfig(state, id) {
            state.configs = state.configs.filter(conf => conf.id !== id)
        },

        setProfile(state, newProfile) {
            state.profile = newProfile
        },

        setProfilesIds(state, profilesIds) {
            state.profilesIds = profilesIds
        },

        setConfigs(state, newConfigs) {
            state.configs = newConfigs
        },

        setSelectedConfig(state, config) {
            state.selectedConfig = config
        },
    },

    actions: {
        createConfig(state, newConfig) {
            return axios.post(process.env.VUE_APP_API_URL + "/configs", newConfig).then((resp) => {
                state.commit('appendConfigs', {
                    ...newConfig,
                    id: resp.data,
                });
                return true
            }).catch((reason) => {
                console.error(reason);
                return false;
            })
        },

        getConfigs(state) {
            return axios.get(process.env.VUE_APP_API_URL + "/configs").then((resp) => {
                state.commit('setConfigs', resp.data?.['configs'])
                return true;
            }).catch((reason) => {
                console.error(reason)
                return false;
            })
        },

        getProfilesIds(state) {
            return axios.get(process.env.VUE_APP_API_URL + "/profile/ids").then((resp) => {
                state.commit('setProfilesIds', resp.data)
                return true;
            }).catch((reason) => {
                console.error(reason)
                return false;
            })
        },

        deleteConfig(state, id) {
            return axios.delete(process.env.VUE_APP_API_URL + "/configs/" + id).then((resp) => {
                state.commit('deleteConfig', id);
                return resp;
            }).catch((reason) => {
                console.error(reason)
                return false
            })

        },

        setSelectedConfig(state, config) {
            state.commit('setSelectedConfig', config);
        }
    },

    getters: {
        getConfigs(state) {
            return state.configs
        },

        getProfileWithIndex(state, idx) {
            return state.profilesIds[idx]
        }
    }
})

export default store;