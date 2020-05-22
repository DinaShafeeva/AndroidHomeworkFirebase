package com.example.androidmvp

import androidx.recyclerview.widget.RecyclerView
import com.example.androidmvp.presenters.MainPresenter
import com.example.androidmvp.recycler.News
import com.example.androidmvp.recycler.NewsAdapter
import com.example.androidmvp.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`


class MainPresenterTest {

    var presenter: MainPresenter? = null

    @Before
    fun beforeFun() {
        val authRepositoryImpl: AuthRepositoryImpl = Mockito.mock(AuthRepositoryImpl::class.java)
        presenter = MainPresenter(authRepositoryImpl)
    }

    @Test
    @Throws(NullPointerException::class)
    fun crashTest() {
       presenter?.causeCrash()
    }

    @Test
    fun addItemInListTest(){
        presenter?.changesFromDialog("name", "desc", 100)
        Assert.assertEquals("name",
            presenter?.getDataSource()?.size?.let { presenter?.getDataSource()?.get(it-1)?.name })
    }

    @Test
    fun deleteItemTest(){
        val size: Int? = presenter?.getDataSource()?.size?.minus(1)
        presenter?.getDataSource()?.get(0)?.let { presenter?.delete(it) }
        Assert.assertEquals(size, presenter?.getDataSource()?.size)
    }

    @Test
    fun getDataSourceTest(){
        Assert.assertEquals("Tittle1", presenter?.getDataSource()?.get(0)?.name)
    }
}
