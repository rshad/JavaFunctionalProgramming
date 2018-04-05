import srcPackage.GlobalCommonVaraibles;
import org.junit.BeforeClass;
import org.junit.Test;
import srcPackage.EmployeesList;
import java.io.IOException;



public class TestListP1 {
    private static EmployeesList Emplist;

    /**
     * Codigo a ejecutar antes de realizar las llamadas a los metodos
     * de la clase; incluso antes de la propia instanciacion de la
     * clase. Por eso el metodo debe ser estatico
     */
    @BeforeClass
    public static void inicializacion() throws IOException {
        System.out.println("Tests of Intialization Methods");
        // Se genera el listado de empleados
        try {
            Emplist = new EmployeesList(GlobalCommonVaraibles.employee_data_path);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Test para comprobar que se ha leido de forma correcta la
     * informacion de los empleados
     *
     * @throws Exception
     */
    @Test
    public void testConstruccionListado() throws Exception {
        assert (Emplist.CountEmployeesInFile() == 5000);
    }

    /**
     * Test para comprobar la deteccion de dnis repetidos
     */
    @Test
    public void testComprobarHayDnisRepetidos() {

        assert (Emplist.detectDuplicatesDni());
    }

    /**
     * Test para comprobar el numero de empleados con correos
     * repetidos
     */
    @Test
    public void testComprobarContadoresDnisRepetidosArchivo() {

        assert (Emplist.countDuplicatesDni() == 4);
    }
}