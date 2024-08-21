$(document).ready(function () {
    $("#btnCancel").on("click", function () {
        window.location = moduleURL;
    })

    $("#fileImage").change(function () {
        const fileSize = this.files[0].size;

        if (fileSize > 1024 * 1024) {
            this.setCustomValidity("Image can not be larger than 1MB")
            this.reportValidity();
        } else {
            this.setCustomValidity("")
            showImageAvatar(this);
        }
    })
})

function showImageAvatar(fileInput) {
    const file = fileInput.files[0];
    const reader = new FileReader();
    reader.onload = (e) => {
        $("#thumbnail").attr("src", e.target.result);
    }

    reader.readAsDataURL(file);
}