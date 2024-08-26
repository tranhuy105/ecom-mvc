$(document).ready(function () {
    const loadCountryButton = $("#loadCountryForStatesButton");
    const countriesDropdown = $("#countriesSelectForStates");
    const statesDropDown = $("#statesSelect");
    const toastDiv = $(".toast")
    const toastMessage = $("#toastMessage")
    const addButton = $("#addStateButton");
    const updateButton = $("#updateStateButton");
    const deleteButton = $("#deleteStateButton");
    const stateName = $("#fieldStateName");

    const url = contextPath  + "states";


    loadCountryButton.on("click", function () {
        loadCountries();
    })

    countriesDropdown.on("change", function () {
        loadStates();
    })

    addButton.on("click", function () {
        if ($(this).val() === "Add") {
            addState();
        } else {
            changeFormStateToNew();
        }
    })

    updateButton.on("click", function () {
        updateState();
    })

    statesDropDown.on("change", function () {
        changeFormStateToSelectedState();
    })

    deleteButton.on("click", function () {
        deleteState();
    })


    function changeFormStateToSelectedState() {
        addButton.prop("value", "New");
        updateButton.prop("disabled", false);
        deleteButton.prop("disabled", false);

        const selectedOption = statesDropDown.find(":selected");
        const text = selectedOption.text();
        stateName.val(text || "");
    }

    function changeFormStateToNew() {
        addButton.prop("value", "Add");
        updateButton.prop("disabled", true);
        deleteButton.prop("disabled", true);

        resetForm();
        stateName.focus();
    }

    function loadCountries() {
        const CountryUrl = contextPath + "countries"

        $.get(CountryUrl, function (res) {
            countriesDropdown.empty()

            console.log(res)

            $.each(res, function (index, country) {
                const optionValue = country.id;
                countriesDropdown.append($("<option>").val(optionValue).text(country.name));
            })
        }).done(function () {
            loadCountryButton.val("Refresh Countries List.");
            showToastMessage("All countries has been loaded");
        }).fail(function () {
            showToastMessage("Something went wrong. Please try again later");
        })
    }

    function loadStates() {
        const countryId = getSelectedCountryId()

        if (countryId === undefined || countryId === null) {
            showToastMessage("Don't do that my friend.")
            return;
        }

        const stateUrl= `countries/${countryId}/states`;
        $.get(stateUrl, function (res) {
            statesDropDown.empty()

            console.log(res)

            $.each(res, function (index, state) {
                const optionValue = state.id;
                statesDropDown.append($("<option>").val(optionValue).text(state.name));
            })
        }).done(function () {
            showToastMessage("All states has been load for country "+countryId);
        }).fail(function () {
            showToastMessage("Something went wrong. Please try again later");
        })
    }

    function addNewlyCreatedState(newState) {
        const optionValue = newState.id;
        statesDropDown.append($("<option>").val(optionValue).text(newState.name));
        resetForm()
    }

    function addState() {
        const name = stateName.val();

        if (!name) {
            showToastMessage("please add a name");
            stateName.focus();
            return;
        }

        const countryId = getSelectedCountryId();

        if (!countryId) {
            showToastMessage("Don't do that my friend.")
            return;
        }

        const jsonData = {name, countryId};

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
            addNewlyCreatedState(country);
        }).fail(function () {
            showToastMessage("Something went wrong. Please try again later.")
        })
    }
    //
    function updateState() {
        const name = stateName.val();
        const countryId = getSelectedCountryId();
        const id = getSelectedStateId();

        if (!name) {
            showToastMessage("please add a name");
            stateName.focus();
            return;
        }

        if (!countryId || !id) {
            showToastMessage("Don't do that my friend.")
            return;
        }

        const jsonData = {countryId, name, id};

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
            const selectedOption = statesDropDown.find(":selected");
            selectedOption.text(jsonData.name)
            selectedOption.val(jsonData.id)
            changeFormStateToNew();
        }).fail(function () {
            showToastMessage("Something went wrong. Please try again later.")
        })
    }

    function deleteState() {
        const id = getSelectedStateId();

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
            statesDropDown.find(":selected").remove()
            showToastMessage("State Deleted.")
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
        stateName.val("")
    }

    function getSelectedCountryId() {
        const id = parseInt(countriesDropdown.val());

        if (!id) {
            return null;
        }

        return id;
    }

    function getSelectedStateId() {
        const id = parseInt(statesDropDown.val());

        if (!id) {
            return null;
        }

        return id;
    }
})