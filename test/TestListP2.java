import srcPackage.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;


public class TestListP2 {
    private static EmployeesList EmpList;

    /**
     * Codigo a ejecutar antes de realizar las llamadas a los metodos
     * de la clase; incluso antes de la propia instanciacion de la
     * clase. Por eso el metodo debe ser estatico
     */
    @BeforeClass
    public static void inicializacion() throws IOException {
        System.out.println("Metodo inicializacion conjunto pruebas");
        // Se genera el listado de empleados
        try {
            EmpList = new EmployeesList(GlobalCommonVaraibles.employee_data_path);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Se reparan los problemas y se pasan los datos al datos miembro
        // listado

        EmpList.validateFileList();

        // Se leen ahora los archivos de asignaciones de sectores y departamentos
        long errores;
        EmpList.LoadArchive_SectorAssignment(GlobalCommonVaraibles.sector1_path);
        EmpList.LoadArchive_SectorAssignment(GlobalCommonVaraibles.sector2_path);

        EmpList.LoadArchive_RouteAssignment(GlobalCommonVaraibles.route1_path);
        EmpList.LoadArchive_RouteAssignment(GlobalCommonVaraibles.route2_path);
        EmpList.LoadArchive_RouteAssignment(GlobalCommonVaraibles.route3_path);
        EmpList.auxiliary_validate();

        System.out.println(" ------------------------------------------------- ");

    }

    /**
     * Test del procedimiento de asignacion de grupos procesando
     * los archivos de asignacion. Tambien implica la prueba de
     * busqueda de empleados sin grupo asignado en alguna asignatura
     *
     * @throws Exception
     */
    @Test
    public void testBusquedaEmpleadosSinRuta() throws Exception {
        System.out.println("------------------- Employees without Routes Counters Test -----------------");

        // Se obtienen los empleados no asignados a cada asignatura
        // y se comprueba su valor
        int res1, res2, res3;
        res1 = EmpList.searchEmployeesWithoutRoute(Sector.NOSECTOR).size();
        res2 = EmpList.searchEmployeesWithoutRoute(Sector.SECTOR1).size();
        res3 = EmpList.searchEmployeesWithoutRoute(Sector.SECTOR2).size();
        System.out.println("res1: " + res1 + " res2: " + res2 + " res3: " + res3);
        assert (res1 == 418);
        assert (res2 == 432);
        assert (res3 == 399);

        System.out.println(" ------------------------------------------------- ");

    }

    /**
     * Prueba para el procedimiento de conteo de grupos para cada una
     * de las asignaturas
     */
    @Test
    public void testObtenerContadoresSector() {
        System.out.println("------------------- SECTOR - ROUTES Counters Test -----------------");
        // Se comprueba que los valores son DEPNA = 49, DEPSB = 48, DEPSM = 53, DEPSA 90
        Long contadoresReferenciaSector1[] = {401L, 438L, 403L, 432L};
        Long contadoresReferenciaSector2[] = {428L, 425L, 390L, 399L};
        Long contadoresReferenciaNoSector[] = {446L, 414L, 409L, 418L};

        Map<Sector,Long[]> sectors = new HashMap<>();

        sectors.put(Sector.SECTOR1,contadoresReferenciaSector1);
        sectors.put(Sector.SECTOR2,contadoresReferenciaSector2);
        sectors.put(Sector.NOSECTOR,contadoresReferenciaNoSector);

        Long contadoresCalculados[];

        for (Sector s: sectors.keySet()){

            System.out.println("Test number 2 - Employees Distribution in Routes of Sector:" + s.toString());
            // Se obtienen los contadores para la asignatura ES
            Map<Route, Long> contadores = EmpList.getCounterRoute(s);

            contadores.keySet().forEach(key -> System.out.println(
                    key.toString() + "- " + contadores.get(key)));

            contadoresCalculados = new Long[4];

            contadoresCalculados[0] = contadores.get(Route.RUTA1);
            contadoresCalculados[1] = contadores.get(Route.RUTA2);
            contadoresCalculados[2] = contadores.get(Route.RUTA3);
            contadoresCalculados[3] = contadores.get(Route.NORUTA);
            assertArrayEquals(contadoresCalculados, sectors.get(s));

            System.out.println(" ----------------------------------- ");
        }

    }




}


