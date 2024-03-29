package co.certificacion.reqres.stepdefinitions;

import co.certificacion.reqres.endpoints.ApiEndPoint;
import co.certificacion.reqres.endpoints.ConsumirApi;
import co.certificacion.reqres.models.AutenticacionModels;
import co.certificacion.reqres.utils.CrearBody;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.es.Cuando;
import cucumber.api.java.es.Dado;
import cucumber.api.java.es.Entonces;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import net.thucydides.core.util.EnvironmentVariables;
import org.hamcrest.Matchers;

import java.util.List;
import java.util.Map;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;


public class ConsumeServicesStep {
    private EnvironmentVariables envVars;
    private Actor analista;

    @Before
    public void configurarElEscenario() {

        String baseUrl = envVars.optionalProperty("restapi.baseurl").orElseThrow(IllegalArgumentException::new);
        SerenityRest.useRelaxedHTTPSValidation("TLS");
        OnStage.setTheStage(new OnlineCast());
        OnStage.theActorCalled("Analista"); //no se hace nada!!!
        analista = Actor.named("Elvis").whoCan(CallAnApi.at(baseUrl));

    }

    @Dado("el Analista envia la peticion con los datos:")
    public void elAnalistaEnviaLaPeticionConLosDatos(List<Map<String, String>> datos) {

        String bodyRegistrarCliente = CrearBody.conLaPlantilla(AutenticacionModels.CREAR_CLIENTE).yLosValores(datos);
        theActorInTheSpotlight().remember("bodyRegistrarCliente", bodyRegistrarCliente);

    }

    @Cuando("que el Analista desea consumir el servicio llamado '(.*)'")
    public void queElAnalistaDeseaConsumirElServicioLlamado(String servicio) {
      String bodyCrearCliente = theActorInTheSpotlight().recall("bodyRegistrarCliente");
      ConsumirApi.tipoPOST(ApiEndPoint.obtenerEndPoint(servicio), bodyCrearCliente, analista);
    }

    @Entonces("el deberia ver que la respuesta es un codigo '(.*)'")
    public void elDeberiaVerQueLaRespuestaEsUnCodigo(int codigo) {
        analista.should(
                seeThatResponse("El usuario se registró correctamente",
                        response -> response.statusCode(codigo))
        );
    }


    @Dado("El analista consulta el servicio de consultar '(.*)'")
    public void elAnalistaConsultaElServicioDeConsultar(String servicio) {
        ConsumirApi.tipoGET(ApiEndPoint.obtenerEndPoint(servicio),analista);

    }
    @Entonces("el mensaje '(.*)' en el campo '(.*)'")
    public void elMensajeEnElCampo(String mensaje, String campo) {
        analista.should(
                seeThatResponse("Estado correcto", lastResponse -> lastResponse.body(campo, Matchers.is(mensaje))));
    }

    @Entonces("el mensaje debe mostrar el valor (.*) en el campo '(.*)'")
    public void elMensajedebeMostrarelvalorEnElCampo(int mensaje, String campo) {
        analista.should(
                seeThatResponse("Estado correcto", lastResponse -> lastResponse.body(campo, Matchers.is(mensaje))));
    }

    @Cuando("El analista realiza la consulta de '(.*)' cliente con el siguiente Id '(.*)'")
    public void elAnalistaRealizaLaConsultaDeClienteConElSiguienteId(String servicio, String id) {
        ConsumirApi.tipoDeleteId(ApiEndPoint.obtenerEndPoint(servicio), id, analista);
    }


    @Entonces("El Analista obtiene como codigo de respuesta '(.*)'")
    public void elAnalistaObtieneComoCodigoDeRespuesta(int codigo) {
        analista.should(
                seeThatResponse("El usuario se registró correctamente",
                        response -> response.statusCode(codigo))
        );
    }

    @Cuando("El analista realiza la consulta del servicio '(.*)' con el siguiente ID '(.*)'")
    public void elAnalistaRealizaLaConsultaDelServicioConElSiguienteID(String servicio, int id) {
        ConsumirApi.tipoGetId(ApiEndPoint.obtenerEndPoint(servicio), id, analista);
    }

    @Dado("el Analista envia la peticion de actualizacion con los datos:")
    public void elAnalistaEnviaLaPeticionDeActualizacionConLosDatos(List<Map<String, String>> datos) {
        String bodyActualizarCliente = CrearBody.conLaPlantilla(AutenticacionModels.ACTUALIZAR_CLIENTE).
                yLosValores(datos);
        theActorInTheSpotlight().remember("bodyActualizarCliente", bodyActualizarCliente);


    }

    @Cuando("El analista realiza la consulta del servicio  '(.*)' cliente con el ID '(.*)'")
    public void elAnalistaRealizaLaConsultaDelServicioClienteConElID(String servicio, int id) {
        String bodyActualizarCliente = theActorInTheSpotlight().recall("bodyActualizarCliente");
        ConsumirApi.tipoPUT(ApiEndPoint.obtenerEndPoint(servicio),bodyActualizarCliente, id, analista);
    }

    @After
    public void cerrarElEscenario() {

        OnStage.drawTheCurtain();
    }

}
