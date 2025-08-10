package com.github.stephenwanjala.crspcalckmp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.stephenwanjala.crspcalckmp.dataSource.CRSPDataSource
import com.github.stephenwanjala.crspcalckmp.domain.models.Motorcycle
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    private val _motorcycles = MutableStateFlow<List<Motorcycle>>(emptyList())

    private val _selectedMakeFilter = MutableStateFlow<String?>(null)
    private val _selectedModelFilter = MutableStateFlow<String?>(null)
    private val _selectedFuelFilter = MutableStateFlow<String?>(null)
    private val _selectedTypeFilter = MutableStateFlow<String?>(null)
    private val _selectedTransmissionFilter = MutableStateFlow<String?>(null)
    private val _selectedDriveFilter =
        MutableStateFlow<String?>(null)
    private val _selectedSortType = MutableStateFlow<SortType>(SortType.Make(OrderType.Ascending))

    private val _events: Channel<HomeEvents> = Channel()
    val events = _events.receiveAsFlow()

    val state = combine(
        _vehicles,
//        _motorcycles,
        _selectedMakeFilter,
        _selectedModelFilter,
        _selectedFuelFilter,
        _selectedTypeFilter,
        _selectedTransmissionFilter,
        _selectedDriveFilter,
        _selectedSortType
    ) { flows ->
        val vehicles = flows[0] as List<Vehicle>
//        val motorcycles = flows[1] as List<Motorcycle>
        val make = flows[1] as String?
        val model = flows[2] as String?
        val fuel = flows[3] as String?
        val type = flows[4] as String?
        val transmission = flows[5] as String?
        val drive = flows[6] as String?
        val sortType = flows[7] as SortType

        val filteredVehicles = vehicles.filter { vehicle ->
            (make == null || vehicle.make == make) &&
                    (model == null || vehicle.model == model) &&
                    (fuel == null || vehicle.fuel == fuel) &&
                    (type == null || vehicle.bodyType == type) &&
                    (transmission == null || vehicle.transmission == transmission) &&
                    (drive == null || vehicle.engineCapacity == drive)
        }.let { filtered ->
            when (sortType) {
                is SortType.Make -> {
                    if (sortType.orderType == OrderType.Ascending) {
                        filtered.sortedBy { it.make }
                    } else {
                        filtered.sortedByDescending { it.make }
                    }
                }

                is SortType.Price -> {
                    if (sortType.orderType == OrderType.Ascending) {
                        filtered.sortedBy { it.crsp }
                    } else {
                        filtered.sortedByDescending { it.crsp }
                    }
                }

                is SortType.Seats -> {
                    if (sortType.orderType == OrderType.Ascending) {
                        filtered.sortedBy { it.seating }
                    } else {
                        filtered.sortedByDescending { it.seating }
                    }
                }
            }
        }
        HomeState(
            isLoading = false,
            vehicles = filteredVehicles,
//            motoBikes = motorcycles,
            selectedMakeFilter = make,
            selectedModelFilter = model,
            selectedFuelFilter = fuel,
            selectedTypeFilter = type,
            selectedTransmissionFilter = transmission,
            selectedDriveFilter = drive,
            selectedSortType = sortType
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeState(isLoading = true)
    )

    init {
        loadVehicles()
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            val vehiclesList = CRSPDataSource.loadVehiclesFromResources()
            _vehicles.update {vehiclesList}
//            _motorcycles.update { dataSource.getMotorcycles() }
        }
    }

    fun onFilterOptionSelected(option: FilterOptions, value: String?) {
        when (option) {
            FilterOptions.Make -> _selectedMakeFilter.update { value }
            FilterOptions.Model -> _selectedModelFilter.update { value }
            FilterOptions.Fuel -> _selectedFuelFilter.update { value }
            FilterOptions.Type -> _selectedTypeFilter.update { value }
            FilterOptions.Transmission -> _selectedTransmissionFilter.update { value }
            FilterOptions.Drive -> _selectedDriveFilter.update { value }
            else -> {}
        }

        if (option == FilterOptions.Make && value == null) {
            _selectedModelFilter.update { null }
        }
    }

    fun onSortTypeSelected(sortType: SortType) {
        _selectedSortType.update { sortType }
    }

    fun onClearAllFilters() {
        _selectedMakeFilter.update { null }
        _selectedModelFilter.update { null }
        _selectedFuelFilter.update { null }
        _selectedTypeFilter.update { null }
        _selectedTransmissionFilter.update { null }
        _selectedDriveFilter.update { null }
        _selectedSortType.update { SortType.Make(OrderType.Ascending) }
    }
}

sealed interface HomeEvents {
    data object Error : HomeEvents
}