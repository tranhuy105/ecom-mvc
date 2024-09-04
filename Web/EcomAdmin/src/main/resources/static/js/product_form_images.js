$(document).ready(function() {
    let imageCounter = $('.image-container').length;

    $('#imageInput').on('change', function(event) {
        const files = event.target.files;
        const fileInput = document.getElementById("imageList");
        if (files.length > 0) {
            $.each(files, function(index, file) {
                if (addFile(fileInput, file)) {
                    addImagePreview(file);
                }
            });
        }

        $(this).val('');
    });

    function addImagePreview(file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const imageHtml = `
                    <div class="col-md-3 mb-3" id="imageSection${imageCounter}">
                        <div class="card image-container add" data-id="null" data-name="${file.name}" data-instruction="add" data-main="false">
                            <img src="${e.target.result}" class="card-img-top img-fluid" alt="Image">
                            <div class="card-body text-center">
                                <button type="button" class="btn btn-danger removeImageButton">
                                    <i class="fas fa-trash"></i> Remove
                                </button>
                                <button type="button" class="btn btn-secondary undoImageButton" style="display: none;">
                                    <i class="fas fa-undo"></i> Undo
                                </button>
                                <div class="form-check mt-3">
                                    <input class="form-check-input mainImageCheckbox" type="checkbox" name="mainImage" />
                                    <label class="form-check-label font-weight-bold">Main Image</label>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            $('#imageContainer').append(imageHtml);
            imageCounter++;
        };
        reader.readAsDataURL(file);
    }

    $('#addImageButton').on('click', function() {
        $('#imageInput').click();
    });

    $(document).on('click', '.removeImageButton', function() {
        const $card = $(this).closest('.image-container');
        const imageName = $card.data('name');
        const fileInput = document.getElementById("imageList");

        if ($card.data('id') === null) {
            $card.closest('.col-md-3').remove();
            removeFileFromFileList(fileInput, imageName);
        } else {
            $card.data('instruction', 'remove')
                .removeClass('add keep')
                .addClass('remove');
            $(this).siblings('.undoImageButton').show();
            $card.find('.mainImageCheckbox').prop('disabled', true);
        }
    });

    $(document).on('click', '.undoImageButton', function() {
        const $card = $(this).closest('.image-container');
        if ($card.data('instruction') === 'remove') {
            $card.data('instruction', 'keep')
                .removeClass('remove')
                .addClass('keep');
            $(this).hide();
            $card.find('.mainImageCheckbox').prop('disabled', false);
        }
    });

    $(document).on('change', '.mainImageCheckbox', function() {
        if ($(this).is(':checked')) {
            $('.mainImageCheckbox').not(this).prop('checked', false);
            $('.image-container').data('main', false);
            $(this).closest('.image-container').data('main', true);
        } else {
            $(this).prop('checked', true);
        }
    });

    $('#productForm').on('submit', function(e) {
        const imageInstructions = [];
        $('.image-container').each(function() {
            const instruction = $(this).data('instruction');
            const isMain = $(this).data('main');
            imageInstructions.push({
                id: $(this).data('id'),
                name: $(this).data('name'),
                instruction: instruction,
                main: isMain
            });
        });
        $('#imageInstructions').val(JSON.stringify(imageInstructions));
    });

    function removeFileFromFileList(input, name) {
        const dt = new DataTransfer();
        const { files } = input;

        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            if (file.name !== name) {
                dt.items.add(file);
            }
        }

        input.files = dt.files;
    }

    function addFile(input, newFile) {
        const dt = new DataTransfer();
        const { files } = input;

        for (let i = 0; i < files.length; i++) {
            if (files[i].name === newFile.name) {
                console.log("already have that file", newFile.name);
                return false;
            }
            dt.items.add(files[i]);
        }

        dt.items.add(newFile);
        input.files = dt.files;
        return true;
    }
});