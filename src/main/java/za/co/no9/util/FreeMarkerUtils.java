package za.co.no9.util;

import freemarker.template.*;

import java.io.*;
import java.util.Map;

public final class FreeMarkerUtils {
    private static FreeMarkerUtils INSTANCE = new FreeMarkerUtils();

    private Configuration cfg = new Configuration();

    private FreeMarkerUtils() {
        cfg = new Configuration();

        cfg.setClassForTemplateLoading(FreeMarkerUtils.class, "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setIncompatibleImprovements(new Version(2, 3, 20));
    }

    public static String template(Map<String, Object> dataModel, String templateName) throws TemplateException {
        try {
            Template template = INSTANCE.cfg.getTemplate(templateName);
            return processTemplate(dataModel, template);
        } catch (IOException ex) {
            throw new TemplateException(ex, null);
        }
    }

    public static String stringTemplate(Map<String, Object> dataModel, String templateName, String templateContent) throws TemplateException {
        try {
            Template template = new Template(templateName, new StringReader(templateContent), INSTANCE.cfg);
            return processTemplate(dataModel, template);
        } catch (IOException ex) {
            throw new TemplateException(ex, null);
        }
    }

    private static String processTemplate(Map<String, Object> dataModel, Template template) throws IOException, TemplateException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Writer out = new OutputStreamWriter(baos)) {
            template.process(dataModel, out);
            return baos.toString();
        }
    }
}