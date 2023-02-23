import {createStore} from 'vuex'
import axiosCreate from "axios";
import {generateRandomProfiles} from "@/components/shared/utils/random-profile-generation/random-profile-generation";
import * as process from "process";

const axios = axiosCreate.create({
    timeout: 20000,
});

axios.interceptors.request.use((config) => {
    config.headers['request-start-time'] = Date.now();
    config.headers['is-retry'] = true
    return config
})

axios.interceptors.response.use((response) => {
    const start = response.config.headers['request-start-time']
    response.headers['request-duration'] = Date.now() - start
    return response
})

let requestTimeData = {}
let totalRequestCount = {}

const store = createStore({
    state: {
        profile: {},
        profilesIds: [],
        configs: [],
        selectedConfig: null,
        testingState: "stop",
        requestsTime: [],
        initTime: Date.now()
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

        addStats(state, resp) {
            let reqDuration = 0;

            if (resp.headers?.['request-duration']) {
                reqDuration = resp.headers['request-duration']
            } else {
                reqDuration = Date.now() - resp.config.headers['request-start-time']
            }

            const timeSlot = Math.floor((parseFloat(resp.config.headers['request-start-time']) - state.initTime) / 1000)
            if (!requestTimeData?.[timeSlot])
                requestTimeData[timeSlot] = {
                    total: 0,
                    n: 0
                }
            requestTimeData[timeSlot].total = reqDuration + requestTimeData[timeSlot].total
            requestTimeData[timeSlot].n = requestTimeData[timeSlot].n + 1

            if (totalRequestCount?.[`${resp.status}`]) {
                totalRequestCount[`${resp.status}`]++;
            } else {
                totalRequestCount[`${resp.status}`] = 1;
            }
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
            }).catch(() => {
                //console.error(reason);
                return false;
            })
        },

        createRandomProfile({commit}) {
            const newProfile = {
                "id": "",
                ...generateRandomProfiles()
            }
            return axios.post(process.env.VUE_APP_API_URL + "/profile", newProfile).then((resp) => {
                commit('appendProfileId', resp.data);
                commit('addStats', resp)
                return true
            }).catch((reason) => {
                //console.error(reason);
                if (reason?.response) {
                    commit('addStats', reason.response)
                } else {
                    commit('addStats', {
                        "headers": {
                            "request-duration": 20000
                        },
                        "config": {
                            "headers": {"request-start-time": Date.now() - 20000}
                        },
                        "status": 504
                    })
                }
                return false;
            })
        },

        updateRandomProfile({state, commit}) {
            if (state.profilesIds.length === 0)
                return false;
            const profileIdIndex = Math.floor(Math.random() * state.profilesIds.length)
            const newProfile = {
                "id": state.profilesIds[profileIdIndex],
                ...generateRandomProfiles()
            }
            return axios.put(process.env.VUE_APP_API_URL + "/profile", newProfile).then((resp) => {
                commit('addStats', resp)
                return resp.data
            }).catch((reason) => {
                //console.error(reason);
                if (reason?.response) {
                    commit('addStats', reason.response)
                } else {
                    commit('addStats', {
                        "headers": {
                            "request-duration": 20000
                        },
                        "config": {
                            "headers": {"request-start-time": Date.now() - 20000}
                        },
                        "status": 504
                    })
                }
                return false;
            })
        },

        getRandomProfile({state, commit}) {
            if (state.profilesIds.length === 0)
                return false;
            const profileIdIndex = Math.floor(Math.random() * state.profilesIds.length)
            return axios.get(process.env.VUE_APP_API_URL + `/profile/${state.profilesIds.at(profileIdIndex)}`).then((resp) => {
                commit('addStats', resp)
                return true;
            }).catch((reason) => {
                //console.error(reason)
                if (reason?.response) {
                    commit('addStats', reason.response)
                } else {
                    commit('addStats', {
                        "headers": {
                            "request-duration": 20000
                        },
                        "config": {
                            "headers": {"request-start-time": Date.now() - 20000}
                        },
                        "status": 504
                    })
                }
                return false;
            })
        },

        getConfigs(state) {
            return axios.get(process.env.VUE_APP_API_URL + "/configs").then((resp) => {
                state.commit('setConfigs', resp.data?.['configs'])
                return true;
            }).catch(() => {
                //console.error(reason)
                return false;
            })
        },

        getProfilesIds(state) {
            return axios.get(process.env.VUE_APP_API_URL + "/profile/ids").then((resp) => {
                state.commit('setProfilesIds', resp.data)
                return true;
            }).catch(() => {
                //console.error(reason)
                return false;
            })
        },

        getProfilesCount({commit}) {
            return axios.get(process.env.VUE_APP_API_URL + "/profile/count").then((resp) => {
                commit('addStats', resp)
                return true;
            }).catch((reason) => {
                //console.error(reason)
                if (reason?.response) {
                    commit('addStats', reason.response)
                } else {
                    commit('addStats', {
                        "headers": {
                            "request-duration": 20000
                        },
                        "config": {
                            "headers": {"request-start-time": Date.now() - 20000}
                        },
                        "status": 504
                    })
                }
                return false;
            })
        },

        deleteConfig(state, id) {
            return axios.delete(process.env.VUE_APP_API_URL + "/configs/" + id).then((resp) => {
                state.commit('deleteConfig', id);
                return resp;
            }).catch(() => {
                //console.error(reason)
                return false
            })

        },

        deleteRandomProfile({state, commit}) {
            if (state.profilesIds.length === 0)
                return false;
            const profileIdIndex = Math.floor(Math.random() * state.profilesIds.length)
            return axios.delete(process.env.VUE_APP_API_URL + `/profile/${state.profilesIds.at(profileIdIndex)}`).then((resp) => {
                commit('deleteProfileId', profileIdIndex)
                commit('addStats', resp)
                return true;
            }).catch((reason) => {
                //console.error(reason)
                if (reason?.response) {
                    commit('addStats', reason.response)
                } else {
                    commit('addStats', {
                        "headers": {
                            "request-duration": 20000
                        },
                        "config": {
                            "headers": {"request-start-time": Date.now() - 20000}
                        },
                        "status": 504
                    })
                }
                return false;
            })
        },

        setSelectedConfig(state, config) {
            state.commit('setSelectedConfig', config);
        },

        setTestingState(state, testingState) {
            state.commit('setTestingState', testingState);
        },

        getRequestsTime({state}) {
            return state.requestsTime
        },

        resetAll({state}) {
            state.requestsTime = []
            requestTimeData = {}
            state.initTime = Date.now()
            totalRequestCount = {}
        },

        getTimeEntry({state}) {
            state.requestsTime = Object.keys(requestTimeData).map((key) => {
                return {
                    "x": key,
                    "y": requestTimeData[key].total / requestTimeData[key].n
                }
            })
            return state.requestsTime
        },

        getRespEntry() {
            return totalRequestCount
        }
    },

    getters: {

        getRequestCount() {
            return totalRequestCount
        },

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
        },
    }
})

export default store;