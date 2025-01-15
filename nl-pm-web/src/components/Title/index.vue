<template>
  <div class="content_title">
      <div class="title">
        <span class="content_text">
          <slot name="title"></slot>
        </span>
      </div>
    </div>
</template>

<script>
export default {
}
</script>

<style scoped lang="scss">
.content_title{
    height: 56px;
    background-color: #fff;
    font-size: 18px;
    font-weight: 600;
    padding: 2px 0 0px 1px;
    // margin-left: 16px;
    margin-right: 16px;
    border-bottom: 1px solid #eee;
    line-height: 31px;
    .title{
      margin-left: 55px;
      .content-text{
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        width: 50%;
        margin-left: 50px;
        margin-top: 20px;
      }
    }
  }
</style>