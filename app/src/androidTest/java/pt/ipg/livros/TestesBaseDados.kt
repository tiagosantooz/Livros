package pt.ipg.livros

import android.provider.BaseColumns
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestesBaseDados {
    private fun getAppContext() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun getBdLivrosOpenHelper() = BdLivrosOpenHelper(getAppContext())

    private fun insereCategoria(tabela: TabelaCategorias, categoria: Categoria): Long {
        val id = tabela.insert(categoria.toContentValues())
        assertNotEquals(-1, id)

        return id
    }

    private fun getCategoriaBaseDados(tabela: TabelaCategorias, id: Long): Categoria {
        val cursor = tabela.query(
            TabelaCategorias.TODAS_COLUNAS,
            "${BaseColumns._ID}=?",
            arrayOf(id.toString()),
            null, null, null
        )

        assertNotNull(cursor)
        assert(cursor!!.moveToNext())

        return Categoria.fromCursor(cursor)
    }

    @Before
    fun apagaBaseDados() {
        getAppContext().deleteDatabase(BdLivrosOpenHelper.NOME_BASE_DADOS)
    }

    @Test
    fun consegueAbrirBaseDados() {
        val db = getBdLivrosOpenHelper().readableDatabase
        assert(db.isOpen)
        db.close()
    }

    @Test
    fun consegueInserirCategorias() {
        val db = getBdLivrosOpenHelper().writableDatabase
        val tabelaCategorias = TabelaCategorias(db)

        val categoria = Categoria(nome = "Drama")
        categoria.id = insereCategoria(tabelaCategorias, categoria)

        assertEquals(categoria, getCategoriaBaseDados(tabelaCategorias, categoria.id))

        db.close()
    }

    @Test
    fun consegueAlterarCategorias() {
        val db = getBdLivrosOpenHelper().writableDatabase
        val tabelaCategorias = TabelaCategorias(db)

        val categoria = Categoria(nome = "sci")
        categoria.id = insereCategoria(tabelaCategorias, categoria)

        categoria.nome = "Fic????o cient??fica"

        val registosAlterados = tabelaCategorias.update(
            categoria.toContentValues(),
            "${BaseColumns._ID}=?",
            arrayOf(categoria.id.toString())
        )

        assertEquals(1, registosAlterados)

        assertEquals(categoria, getCategoriaBaseDados(tabelaCategorias, categoria.id))

        db.close()
    }

    @Test
    fun consegueEliminarCategorias() {
        val db = getBdLivrosOpenHelper().writableDatabase
        val tabelaCategorias = TabelaCategorias(db)

        val categoria = Categoria(nome = "teste")
        categoria.id = insereCategoria(tabelaCategorias, categoria)

        val registosEliminados = tabelaCategorias.delete(
            "${BaseColumns._ID}=?",
            arrayOf(categoria.id.toString())
        )

        assertEquals(1, registosEliminados)

        db.close()
    }

    @Test
    fun consegueLerCategorias() {
        val db = getBdLivrosOpenHelper().writableDatabase
        val tabelaCategorias = TabelaCategorias(db)

        val categoria = Categoria(nome = "Aventura")
        categoria.id = insereCategoria(tabelaCategorias, categoria)

        assertEquals(categoria, getCategoriaBaseDados(tabelaCategorias, categoria.id))

        db.close()
    }
}