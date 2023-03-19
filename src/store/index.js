import {createStore} from 'vuex'
import axiosCreate from "axios";
import * as process from "process";

const axios = axiosCreate.create({
    timeout: 20000,
});

const store = createStore({
    state: {
        profile: {},
        configs: [],
        selectedConfig: null,
        testingState: "stop",
        requestTimeData: {},
        responseCodes: {},
        successRateData: {},
        requests: 0,
        retryRequests: 0,
    },

    mutations: {
        appendConfigs(state, newConfig) {
            state.configs.push(newConfig);
        },

        deleteConfig(state, id) {
            state.configs = state.configs.filter(conf => conf.id !== id)
        },

        setConfigs(state, newConfigs) {
            state.configs = newConfigs
        },

        setSelectedConfig(state, config) {
            state.selectedConfig = config
        },

        setTestingState(state, testingState) {
            state.testingState = testingState
        }
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

        getConfigs(state) {
            return axios.get(process.env.VUE_APP_API_URL + "/configs").then((resp) => {
                state.commit('setConfigs', resp.data?.['configs'])
                return true;
            }).catch(() => {
                //console.error(reason)
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

        setSelectedConfig(state, config) {
            axios.put(process.env.VUE_APP_API_URL + `/configs/${config.id}`).then(() => {
                state.commit('setSelectedConfig', config);
            }).catch(() => {
                return false
            })
        },

        setTestingState(state, testingState) {
            axios.post(process.env.VUE_APP_API_URL + `/testing/${testingState}`).then(() => {
                state.commit('setTestingState', testingState);
            }).catch(() => {
                return false
            })
        },

        gatherStats({ state }) {
            axios.get(process.env.VUE_APP_API_URL + '/testing/stats').then((resp) => {
                state.responseCodes = resp.data.responseCodes;
                state.requestTimeData = resp.data.averageTimeForRequestsPerSecondByTime;
                state.successRateData = resp.data.successRateOfRequestsBySeconds;
                state.requests = resp.data.currentRequests;
                state.retryRequest = resp.data.currentRetryRequests;
            })
        },

        getRequests({ state }) {
            return {
                requests: state.requests,
                retryRequests: state.retryRequests
            }
        },

        getTimeEntry({ state }) {
            return Object.keys(state.requestTimeData).map((key) => {
                return {
                    "x": key,
                    "y": state.requestTimeData[key].second / state.requestTimeData[key].first
                }
            })
        },

        getSuccessEntry({ state }) {
            return Object.keys(state.requestTimeData).map((key) => {
                return {
                    "x": key,
                    "y": (100.0 * state.successRateData[key].second) / state.successRateData[key].first
                }
            })
        },

        getRespEntry({ state }) {
            return state.responseCodes
        }
    },

    getters: {
        getConfigs(state) {
            return state.configs
        },

        getSelectedConfigs(state) {
            return state.selectedConfig
        },

        getTestingState(state) {
            return state.testingState
        },
    }
})

export default store;