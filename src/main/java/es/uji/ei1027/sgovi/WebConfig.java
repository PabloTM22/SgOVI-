package es.uji.ei1027.sgovi;

import es.uji.ei1027.sgovi.interceptor.IdentificacionInterceptor;
import es.uji.ei1027.sgovi.interceptor.RolInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.file.directory}")
    private String uploadDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/pdfs/**")
                .addResourceLocations("file:" + uploadDirectory + "pdfs/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Zonas exclusivas del TECNICO
        registry.addInterceptor(new RolInterceptor("TECNICO"))
                .addPathPatterns("/tecnico/**")
                .addPathPatterns("/usuarios/**")
                .addPathPatterns("/formadores/**")
                .addPathPatterns("/candidatos/revision")
                .addPathPatterns("/candidatos/aceptar/**")
                .addPathPatterns("/candidatos/rechazar/**")
                .addPathPatterns("/candidatos/detalle/**");

        // Zona exclusiva del CANDIDATO (asistente personal)
        registry.addInterceptor(new RolInterceptor("CANDIDATO"))
                .addPathPatterns("/mi-perfil/**");

        // Zonas que solo requieren estar autenticado (cualquier rol)
        registry.addInterceptor(new IdentificacionInterceptor())
                .addPathPatterns("/solicitudes/**")
                .addPathPatterns("/contratos/**")
                .addPathPatterns("/comunicaciones/**")
                .addPathPatterns("/asistentes/**");
    }
}