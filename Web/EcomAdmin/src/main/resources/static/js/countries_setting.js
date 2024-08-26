$(document).ready(function () {
    const loadButton = $("#loadCountryButton");
    const countriesDropdown = $("#countriesSelect");
    const toastDiv = $(".toast")
    const toastMessage = $("#toastMessage")
    const addButton = $("#addCountryButton");
    const updateButton = $("#updateCountryButton");
    const deleteButton = $("#deleteCountryButton");

    const countryName = $("#fieldCountryName");
    const countryCode = $("#fieldCountryCode");

    const url = contextPath  + "countries";


    loadButton.on("click", function () {
        loadCountries();
    })

    addButton.on("click", function () {
        if ($(this).val() === "Add") {
            addCountry();
        } else {
            changeFormStateToNew();
        }
    })

    updateButton.on("click", function () {
        updateCountry();
    })

    countriesDropdown.on("change", function () {
        changeFormStateToSelectedCountry();
    })

    deleteButton.on("click", function () {
        deleteCountry();
    })


    function changeFormStateToSelectedCountry() {
        addButton.prop("value", "New");
        updateButton.prop("disabled", false);
        deleteButton.prop("disabled", false);

        const selectedOption = countriesDropdown.find(":selected");
        const selectedValue = selectedOption.val();
        const selectedText = selectedOption.text();
        const [id, code] = selectedValue.split(" - ");
        countryName.val(selectedText || "");
        countryCode.val(code || "");
    }

    function changeFormStateToNew() {
        addButton.prop("value", "Add");
        updateButton.prop("disabled", true);
        deleteButton.prop("disabled", true);

        resetForm();
        countryName.focus();
    }

    function loadCountries() {
        $.get(url, function (res) {
            countriesDropdown.empty()

            console.log(res)

            $.each(res, function (index, country) {
                const optionValue = country.id + " - " + country.code;
                countriesDropdown.append($("<option>").val(optionValue).text(country.name));
            })
        }).done(function () {
            loadButton.val("Refresh Countries List.");
            showToastMessage("All countries has been loaded");
        }).fail(function () {
            showToastMessage("Something went wrong. Please try again later");
        })
    }

    function addNewlyCreatedCountry(newCountry) {
        console.log(newCountry)
        const optionValue = newCountry.id + " - " + newCountry.code;
        countriesDropdown.append($("<option>").val(optionValue).text(newCountry.name));
        resetForm()
    }

    function addCountry() {
        const name = countryName.val();
        const code = countryCode.val();

        if (!name) {
            showToastMessage("please add a name");
            countryName.focus();
            return;
        }

        if (!code) {
            showToastMessage("please add a code");
            countryCode.focus();
            return;
        } else {
            if (code.length > 4) {
                showToastMessage("code should be at max 4 characters long.")
                countryCode.focus();
                return;
            }
        }

        const jsonData = {name, code};

        $.ajax({
            type: "POST",
            url,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfValue)
            },
            data: JSON.stringify(jsonData),
            contentType: "application/json"
        }).done(function (country) {
            showToastMessage("country added");
            addNewlyCreatedCountry(country);
        }).fail(function () {
            showToastMessage("Something went wrong. Please try again later.")
        })
    }

    function updateCountry() {
        const name = countryName.val();
        const code = countryCode.val();

        if (!name) {
            showToastMessage("name can not be empty");
            countryName.focus();
            return;
        }

        if (!code) {
            showToastMessage("code can not be empty");
            countryCode.focus();
            return;
        } else {
            if (code.length > 4) {
                showToastMessage("code should be at max 4 characters long.")
                countryCode.focus();
                return;
            }
        }

        const id = getSelectedId();

        if (!id) {
            showToastMessage("Don't do that my friend");
            return;
        }

        const jsonData = {id, name, code};

        console.log(jsonData);

        $.ajax({
            type: "POST",
            url,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfValue)
            },
            data: JSON.stringify(jsonData),
            contentType: "application/json"
        }).done(function () {
            showToastMessage("country updated success");
            const selectedOption = countriesDropdown.find(":selected");
            selectedOption.text(jsonData.name)
            selectedOption.val(jsonData.id + " - " + jsonData.code)
            changeFormStateToNew();
        }).fail(function () {
            showToastMessage("Something went wrong. Please try again later.")
        })
    }

    function deleteCountry() {
        const id = getSelectedId();

        if (!id) {
            showToastMessage("Don't do that my friend");
            return;
        }

        const deleteUrl = url + "/" + id;

        $.ajax(deleteUrl, {
            type: "DELETE",
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfValue)
            },
        }).done(function () {
            countriesDropdown.find(":selected").remove()
            showToastMessage("Country Deleted.")
            changeFormStateToNew();
        }).fail(function () {
            showToastMessage("Something went wrong. Please try again later.")
        })
    }

    function showToastMessage(message) {
        toastMessage.text(message)
        toastDiv.toast('show')
    }

    function resetForm() {
        countryName.val("")
        countryCode.val("")
    }

    function getSelectedId() {
        const id = parseInt(countriesDropdown.val().split("-")[0]);

        if (!id) {
            return null;
        }

        return id;
    }
})