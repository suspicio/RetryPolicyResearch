import { createStore } from 'vuex'
import axiosCreate from "axios";
import { generateRandomProfiles } from "@/components/shared/utils/random-profile-generation/random-profile-generation";

const axios = axiosCreate.create({
    timeout: 20000,
});

const store = createStore({
    state: {
        profile: {},
        profilesIds: [],
        configs: [],
        selectedConfig: null,
        testingState: "stop"
    },

    mutations: {
        appendConfigs(state, newConfig) {
            state.configs.push(newConfig);
        },

        appendProfileId(state, profileId) {
            state.profilesIds.push(profileId)
        },

        deleteConfig(state, id) {
            state.configs = state.configs.filter(conf => conf.id !== id)
        },

        deleteProfileId(state, id) {
            state.profilesIds = state.profilesIds.filter(profile => profile.id !== id)
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

        setTestingState(state, testingState) {
            state.testingState = testingState
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

        createRandomProfile(state) {
            const newProfile = {
                "id": "",
                ...generateRandomProfiles()
            }
            return axios.post(process.env.VUE_APP_API_URL + "/profile", newProfile).then((resp) => {
                state.commit('appendProfileId', resp.data);
                return true
            }).catch((reason) => {
                console.error(reason);
                return false;
            })
        },

        updateRandomProfile({state}) {
            if (state.profilesIds.length === 0)
                return false;
            const profileIdIndex = Math.floor(Math.random() * state.profilesIds.length)
            const newProfile = {
                "id": state.profilesIds[profileIdIndex],
                ...generateRandomProfiles()
            }
            return axios.put(process.env.VUE_APP_API_URL + "/profile", newProfile).then((resp) => {
                return resp.data
            }).catch((reason) => {
                console.error(reason);
                return false;
            })
        },

        getRandomProfile({state}) {
            if (state.profilesIds.length === 0)
                return false;
            const profileIdIndex = Math.floor(Math.random() * state.profilesIds.length)
            return axios.get(process.env.VUE_APP_API_URL + `/profile/${state.profilesIds.at(profileIdIndex)}`).then((resp) => {
                console.log(resp.status)
                return true;
            }).catch((reason) => {
                console.error(reason)
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
                console.log(resp);
                state.commit('setProfilesIds', resp.data)
                return true;
            }).catch((reason) => {
                console.error(reason)
                return false;
            })
        },

        getProfilesCount() {
            return axios.get(process.env.VUE_APP_API_URL + "/profile/count").then((resp) => {
                console.log(resp.data)
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

        deleteRandomProfile({state, commit}) {
            if (state.profilesIds.length === 0)
                return false;
            const profileIdIndex = Math.floor(Math.random() * state.profilesIds.length)
            return axios.delete(process.env.VUE_APP_API_URL + `/profile/${state.profilesIds.at(profileIdIndex)}`).then((resp) => {
                commit('deleteProfileId', profileIdIndex)
                console.log(resp.status)
                return true;
            }).catch((reason) => {
                console.error(reason)
                return false;
            })
        },

        setSelectedConfig(state, config) {
            state.commit('setSelectedConfig', config);
        },

        setTestingState(state, testingState) {
            state.commit('setTestingState', testingState);
        }
    },

    getters: {
        getConfigs(state) {
            return state.configs
        },

        getSelectedConfigs(state) {
          return state.selectedConfig
        },

        getProfileWithIndex(state, idx) {
            return state.profilesIds[idx]
        },

        getTestingState(state) {
            return state.testingState
        },

        getCountOfProfiles(state) {
            return state.profilesIds.length
        }
    }
})

export default store;