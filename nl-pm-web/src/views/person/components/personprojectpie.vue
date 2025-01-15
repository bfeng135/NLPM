<template>
  <div>
    <el-row :gutter="32" v-if="deepList.length > 0">
      <el-col
        :xs="24"
        :sm="24"
        :lg="8"
        class="card-panel-col"
        v-for="(item, index) in deepList"
        :key="index"
      >
        <div class="charts-wrapper" @click="drawRose(item)">
          <el-tooltip
            class="item"
            effect="dark"
            :content="item.name"
            placement="top"
          >
            <span class="pie-name">{{ cutName(item.name) }}</span>
          </el-tooltip>
          <div class="roseChart"></div>
        </div>
      </el-col>
    </el-row>
    <el-row v-else style="text-align: center;height:100%">暂无数据</el-row>
  </div>
</template>

<script>
import * as echarts from "echarts";
require("echarts/theme/macarons");
import resize from "@/views/dashboard/components/charts/mixins/resize";
export default {
  mixins: [resize],
  props: {
    list: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      deepList: [],
      deepName: "",
    };
  },
  watch: {
    list(val) {
      this.deepList = val;
      this.deepList.forEach((item) => {
        if (item.resData.length) {
          item.names = [];
          item.resData.forEach((element) => {
            item.names.push(element.name);
          });
        }
      });
    },
  },
  mounted() {
    setTimeout(() => {
      this.$nextTick(()=>{
       this.drawRose();
      })
    }, 1000);
  },
  methods: {
    cutName(value) {
      return value.length > 14 ? value.substring(0, 14) + "..." : value;
    },
    drawRose(name) {
      if (name !== undefined) {
        this.deepName = name.name;
      }
      const that = this;
      var roseCharts = document.getElementsByClassName("roseChart");
      for (var i = 0; i < roseCharts.length; i++) {
        var myChart = echarts.init(roseCharts[i], "macarons");
        myChart.setOption({
          tooltip: {
            trigger: "item",
            formatter: "{a} <br/>{b} : {c} ({d}%)",
            backgroundColor: "rgba(50,50,50,0.6)",
            borderWidth: 0,
            textStyle: {
              color: "#ffffff",
            },
            position: "top",
          },
          legend: {
            left: "center",
            bottom: "5",
            data: this.deepList[i].names,
            type:'scroll'
          },
          series: [
            {
              name: "人员项目工时占比图",
              type: "pie",
              roseType: "radius",
              radius: [15, 95],
              center: ["50%", "45%"],
              data: this.deepList[i].resData,
              animationEasing: "cubicInOut",
              animationDuration: 2600,
            },
          ],
          
        });
        const list = []
        list.push(myChart)
        for(let i=0; i<list.length ;i++){
          window.addEventListener("resize",()=>{
            list[i].resize();
          })
        }
      }
    },
  },
};
</script>

<style>
.charts-wrapper {
  background: #fff;
  padding: 16px 16px 0;
  margin-bottom: 32px;
}
.roseChart {
  width: 100%;
  height: 320px;
}
.pie-name {
  font-size: 18px;
  color: #4ba5d5;
  font-weight: bold;
  opacity: 0.9;
}
</style>