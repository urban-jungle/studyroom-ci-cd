<template>
  <div class="container">
    <h2>예약 목록</h2>

    <!-- 🔍 필터 & 검색 & 다운로드 -->
    <div class="filter-section">
      <label>예약자 이름 검색:</label>
      <input v-model="searchName" placeholder="이름 입력" />

      <label>날짜 필터:</label>
      <input type="date" v-model="filterDate" />

      <button @click="clearFilters">전체 보기</button>
      <button @click="downloadCSV">CSV 다운로드</button>
    </div>

    <!-- 📋 예약 입력 폼 -->
    <ReservationForm
      :editReservation="selectedReservation"
      @reservation-submitted="handleReservationSubmitted"
    />

    <!-- 📊 예약 테이블 -->
    <table>
      <thead>
        <tr>
          <th>예약자</th>
          <th>스터디룸</th>
          <th>시작 시간</th>
          <th>종료 시간</th>
          <th>수정</th>
          <th>삭제</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="reservation in filteredReservations" :key="reservation.id">
          <td>{{ reservation.reserverName }}</td>
          <td>{{ reservation.room.roomName }}</td>
          <td>{{ formatDate(reservation.startTime) }}</td>
          <td>{{ formatDate(reservation.endTime) }}</td>
          <td><button @click="startEdit(reservation)">수정</button></td>
          <td><button @click="deleteReservation(reservation.id)">삭제</button></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from "../axios";
import ReservationForm from "./ReservationForm.vue";

export default {
  name: "ReservationList",
  components: { ReservationForm },
  data() {
    return {
      reservations: [],
      selectedReservation: null,
      filterDate: "",
      searchName: "",
    };
  },
  computed: {
    sortedReservations() {
      return [...this.reservations].sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
    },
    filteredReservations() {
      return this.sortedReservations.filter((r) => {
        const matchDate = this.filterDate ? r.startTime.startsWith(this.filterDate) : true;
        const matchName = this.searchName ? r.reserverName.includes(this.searchName) : true;
        return matchDate && matchName;
      });
    },
  },
  methods: {
    async fetchReservations() {
      const res = await axios.get("/api/reservations");
      this.reservations = res.data;
    },
    formatDate(dateString) {
      const options = {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      };
      return new Date(dateString).toLocaleString("ko-KR", options);
    },
    async deleteReservation(id) {
      if (confirm("정말 삭제하시겠습니까?")) {
        await axios.delete(`/api/reservations/${id}`);
        alert("삭제 완료!");
        this.fetchReservations();
      }
    },
    startEdit(reservation) {
      this.selectedReservation = { ...reservation };
    },
    handleReservationSubmitted() {
      this.fetchReservations();
      this.selectedReservation = null;
    },
    clearFilters() {
      this.filterDate = "";
      this.searchName = "";
    },
    downloadCSV() {
      const headers = "예약자,스터디룸,시작 시간,종료 시간\n";
      const rows = this.filteredReservations
        .map((r) =>
          [
            r.reserverName,
            r.room.roomName,
            this.formatDate(r.startTime),
            this.formatDate(r.endTime),
          ].join(",")
        )
        .join("\n");

      const blob = new Blob([headers + rows], { type: "text/csv;charset=utf-8;" });
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.setAttribute("href", url);
      link.setAttribute("download", "reservations.csv");
      link.click();
    },
  },
  created() {
    this.fetchReservations();
  },
};
</script>

<style scoped>
.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.filter-section {
  margin-bottom: 20px;
}

input,
select,
button {
  margin: 6px;
  padding: 6px;
  font-size: 14px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th,
td {
  border: 1px solid #ccc;
  padding: 10px;
  text-align: center;
}

th {
  background-color: #f3f3f3;
}
</style>
