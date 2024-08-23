function addSku() {
    const newSku = {
        id: null,
        skuCode: $('#newSkuCode').val(),
        price: parseFloat($('#newPrice').val()) || 0,
        discountPercent: parseFloat($('#newDiscountPercent').val()) || 0,
        stockQuantity: parseInt($('#newStockQuantity').val()) || 0,
        length: parseFloat($('#newLength').val()) || 0,
        width: parseFloat($('#newWidth').val()) || 0,
        height: parseFloat($('#newHeight').val()) || 0,
        weight: parseFloat($('#newWeight').val()) || 0,
        productId: parseInt($('#id').val()) || null
    };
    skus.push(newSku);
    updateSkusInput();
    renderSkus();
    clearInputs();
}

let skus = [];

function removeSku(index) {
    skus.splice(index, 1);
    updateSkusInput();
    renderSkus();
}

function updateSkusInput() {
    $('#skusInput').val(JSON.stringify(skus));
}

function renderSkus() {
    const tbody = $('#skuList');
    tbody.empty();
    $.each(skus, function (index, sku) {
        const row = `<tr>
                <td><input type="text" value="${sku.skuCode}" class="form-control" data-index="${index}" data-field="skuCode"/></td>
                <td><input type="number" step="0.01" value="${sku.price}" class="form-control" placeholder="Price" data-index="${index}" data-field="price"/></td>
                <td><input type="number" step="0.01" value="${sku.discountPercent}" class="form-control" placeholder="Discount %" data-index="${index}" data-field="discountPercent"/></td>
                <td><input type="number" value="${sku.stockQuantity}" class="form-control" placeholder="Stock Quantity" data-index="${index}" data-field="stockQuantity"/></td>
                <td>
                    <input type="number" step="0.01" value="${sku.length}" class="form-control mb-1" placeholder="Length" data-index="${index}" data-field="length"/>
                    <input type="number" step="0.01" value="${sku.width}" class="form-control mb-1" placeholder="Width" data-index="${index}" data-field="width"/>
                    <input type="number" step="0.01" value="${sku.height}" class="form-control" placeholder="Height" data-index="${index}" data-field="height"/>
                </td>
                <td><input type="number" step="0.01" value="${sku.weight}" class="form-control" placeholder="Weight" data-index="${index}" data-field="weight"/></td>
                <td>
                    <button type="button" class="btn btn-danger btn-sm" onclick="removeSku(${index})">Remove</button>
                </td>
            </tr>`;
        tbody.append(row);
    });

    $('input[data-index]').on('input', function () {
        const index = $(this).data('index');
        const field = $(this).data('field');
        const value = $(this).val();
        skus[index][field] = field === 'price' || field === 'discountPercent' || field === 'length' || field === 'width' || field === 'height' || field === 'weight' ? parseFloat(value) : value;
        updateSkusInput();
    });
}

function clearInputs() {
    $('#newSkuCode').val('');
    $('#newPrice').val('');
    $('#newDiscountPercent').val('');
    $('#newStockQuantity').val('');
    $('#newLength').val('');
    $('#newWidth').val('');
    $('#newHeight').val('');
    $('#newWeight').val('');
}

$(document).ready(function () {
    const json = $('#skusInput').val();
    try {
        if (json) {
            skus = JSON.parse(json);
        } else {
            skus = []
        }
    } catch (err) {
        console.log(json)
        console.error(err)
    }
    renderSkus();
    updateSkusInput();
});

function submitForm() {
    updateSkusInput();
    $('#productForm').submit();
}