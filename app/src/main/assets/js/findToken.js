(function () {
  fetch("https://www.zhixue.com/addon/error/book/index")
    .then(response => response.json())
    .then((data) => {
      Android.submitToken(data.result)
    })
})()
