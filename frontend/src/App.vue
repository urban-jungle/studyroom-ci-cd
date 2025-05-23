<template>
  <div id="app">
    <ReservationList />
  </div>
</template>

<script>
import ReservationList from "./components/ReservationList.vue";

export default {
  name: "App",
  components: {
    ReservationList,
  },
};
</script>
