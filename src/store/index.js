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
        totalRequestCount: {}
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

        getTimeEntry() {
            return {}
        },

        getSuccessEntry() {
            return {}
        },

        getRespEntry() {
            return {}
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