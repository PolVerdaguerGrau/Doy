package com.napptilians.networkdatasource.api.data

import com.napptilians.networkdatasource.api.mappers.MovieMapper
import com.napptilians.networkdatasource.api.models.MovieApiModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class NetworkDataSourceImplTest {

    init {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @MockK
    private lateinit var movieService: MovieService

    @MockK
    private lateinit var movieMapper: MovieMapper

    @MockK
    private lateinit var movieMock: MovieApiModel

    private val dataSource by lazy {
        NetworkDataSourceImpl(
            movieService,
            movieMapper
        )
    }

    @Before
    fun setup() {
//        val mockMovieListApiModel =
//            Gson().fromJson(MockObjects.movieListJson, MovieListApiModel::class.java)

        coEvery { movieService.getMovie(any()) } returns movieMock
        every { movieMapper.map(any()) } returns mockk()
    }

    @Test
    fun `getMovie() must call API service getMovie() and map the result`() =
        runBlockingTest {
            // Given
            val movieId = 550L

            // When
            dataSource.getMovie(movieId)

            // Then
            coVerify(exactly = 1) {
                movieService.getMovie(movieId)
                movieMapper.map(any())
            }
        }
}
