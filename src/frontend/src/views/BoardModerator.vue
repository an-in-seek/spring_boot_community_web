<template>
  <div>
    <header class="jumbotron mt-4">
      <h3>{{content}}</h3>
    </header>
  </div>
</template>

<script>
import UserService from '../services/user.service';

export default {
  name: 'Moderator',
  data() {
    return {
      content: ''
    };
  },
  mounted() {
    UserService.getModeratorBoard().then(
      response => {
        this.content = response.data;
      },
      error => {
        this.content =
          (error.response && error.response.data.message) ||
          error.message ||
          error.toString();
      }
    );
  }
};
</script>
