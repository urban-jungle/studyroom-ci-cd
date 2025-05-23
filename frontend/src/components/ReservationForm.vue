<template>
  <div class="form-wrapper">
    <h2>스터디룸 예약</h2>
    <form @submit.prevent="submitReservation">
      <div class="form-row">
        <label>예약자 이름:</label>
        <input v-model="reserverName" required />
      </div>

      <div class="form-row">
        <label>시작 시간:</label>
        <input type="datetime-local" v-model="startTime" required />

        <label>종료 시간:</label>
        <input type="datetime-local" v-model="endTime" required />
      </div>

      <div class="form-row">
        <label>스터디룸 선택:</label>
        <select v-model="roomId" required>
          <option v-for="room in rooms" :key="room.id" :value="room.id">
            {{ room.roomName }} (정원: {{ room.capacity }})
          </option>
        </select>

        <button type="submit">{{ isEditMode ? "수정하기" : "예약" }}</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from "../axios";

export default {
  name: "ReservationForm",
  props: {
    editReservation: Object,
  },
  data() {
    return {
      rooms: [],
      id: null,
      reserverName: "",
      startTime: "",
      endTime: "",
      roomId: null,
    };
  },
  computed: {
    isEditMode() {
      return this.id !== null;
    },
  },
  watch: {
    editReservation: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.id = newVal.id;
          this.reserverName = newVal.reserverName;
          this.startTime = newVal.startTime.slice(0, 16);
          this.endTime = newVal.endTime.slice(0, 16);
          this.roomId = newVal.room.id;
        }
      },
    },
  },
  methods: {
    async submitReservation() {
      const data = {
        reserverName: this.reserverName,
        startTime: this.startTime,
        endTime: this.endTime,
        room: { id: this.roomId },
      };

      try {
        if (this.isEditMode) {
          await axios.put(`/api/reservations/${this.id}`, data);
          alert("수정 완료!");
        } else {
          await axios.post("/api/reservations", data);
          alert("예약 완료!");
        }

        this.$emit("reservation-submitted");
        this.resetForm();
      } catch (error) {
        if (error.response && error.response.status === 409) {
          alert(error.response.data);
        } else {
          alert("작업 실패");
        }
      }
    },
    resetForm() {
      this.id = null;
      this.reserverName = "";
      this.startTime = "";
      this.endTime = "";
      this.roomId = null;
    },
  },
  async created() {
    const res = await axios.get("/api/rooms");
    this.rooms = res.data;
  },
};
</script>

<style scoped>
.form-wrapper {
  background: #f9f9f9;
  padding: 16px;
  margin-top: 20px;
  border-radius: 8px;
}

.form-row {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

label {
  min-width: 100px;
}
</style>
