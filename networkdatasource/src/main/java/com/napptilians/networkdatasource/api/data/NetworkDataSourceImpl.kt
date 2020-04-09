package com.napptilians.networkdatasource.api.data

import com.napptilians.commons.Response
import com.napptilians.commons.error.ErrorModel
import com.napptilians.data.datasources.NetworkDataSource
import com.napptilians.domain.models.movie.CategoryModel
import com.napptilians.domain.models.movie.ServiceModel
import com.napptilians.networkdatasource.api.mappers.CategoryMapper
import com.napptilians.networkdatasource.api.mappers.ServiceInMapper
import com.napptilians.networkdatasource.utils.safeApiCall
import javax.inject.Inject

class NetworkDataSourceImpl @Inject constructor(
    private val categoryListService: CategoryListService,
    private val categoryMapper: CategoryMapper,
    private val serviceService: ServiceService,
    private val serviceInMapper: ServiceInMapper
) : NetworkDataSource {

    override suspend fun getCategories(categoryIds: List<Long>): Response<List<CategoryModel>, ErrorModel> {
        return safeApiCall {
            categoryListService.getCategories(categoryIds).map {
                categoryMapper.map(it)
            }
        }
    }

    override suspend fun addService(service: ServiceModel): Response<Long, ErrorModel> {
        return safeApiCall {
            serviceService.addService(serviceInMapper.map(service))
        }
    }
}