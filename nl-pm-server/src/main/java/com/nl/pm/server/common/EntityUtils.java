package com.nl.pm.server.common;

import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityUtils {
    private static final String DOT = ".";
    private static final String PATH_ENTITY = "com.nl.pm.server.registry.entity";
    private static final String PATH_MODEL = "com.nl.pm.server.service.model";
    private static final String MODEL = "Model";
    private static final String ENTITY = "Entity";
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String IS = "is";
    private static final String CLASS = "class";
    private static final String BL = "boolean";
    private static final String BLANK = " ";
    private static final String EMPTY = "";

    public static Object convertModelToEntity(Object model) throws Exception {
        try {
            if (model != null) {
                String modelClassName = model.getClass().getSimpleName();
                String entityClassName = modelClassName.substring(0, modelClassName.indexOf(MODEL)) + ENTITY;
                Object entity = Class.forName(PATH_ENTITY + DOT + entityClassName).newInstance();

                Field[] entityDeclaredFields = entity.getClass().getDeclaredFields();
                List<Field> entityFields = new ArrayList<>(Arrays.asList(entityDeclaredFields));
                Field[] superEntityDeclaredFields = entity.getClass().getSuperclass().getDeclaredFields();
                List<Field> superEntityFields = new ArrayList<>(Arrays.asList(superEntityDeclaredFields));
                entityFields.addAll(superEntityFields);

                Map<String, Object> entityFieldMap = entityFields.stream().collect(Collectors.toMap(Field::getName, Field::getGenericType));

                Method[] modelMethods = model.getClass().getMethods();
                List<Method> modelMethodsList = new ArrayList<>(Arrays.asList(modelMethods));
                Map<String, Method> modelMethodsMap = modelMethodsList.stream().filter(i->(i.getName().startsWith(GET) || i.getName().startsWith(IS) || i.getName().startsWith(SET))).collect(Collectors.toMap(i -> i.getName(), Function.identity()));
                Method[] superModelMethods = model.getClass().getSuperclass().getMethods();
                List<Method> superModelMethodsList = new ArrayList<>(Arrays.asList(superModelMethods));
                Map<String, Method> superModelMethodsMap = superModelMethodsList.stream().filter(i->(i.getName().startsWith(GET) || i.getName().startsWith(IS) || i.getName().startsWith(SET))).collect(Collectors.toMap(i -> i.getName(), Function.identity()));
                modelMethodsMap.putAll(superModelMethodsMap);


                Method[] entityMethods = entity.getClass().getMethods();
                List<Method> entityMethodsList = new ArrayList<>(Arrays.asList(entityMethods));
                Map<String, Method> entityMethodsMap = entityMethodsList.stream().filter(i->(i.getName().startsWith(GET) || i.getName().startsWith(IS) || i.getName().startsWith(SET))).collect(Collectors.toMap(i -> i.getName(), Function.identity()));
                Method[] superEntityMethods = entity.getClass().getSuperclass().getMethods();
                List<Method> superEntityMethodsList = new ArrayList<>(Arrays.asList(superEntityMethods));
                Map<String, Method> superEntityMethodsMap = superEntityMethodsList.stream().filter(i->(i.getName().startsWith(GET) || i.getName().startsWith(IS) || i.getName().startsWith(SET))).collect(Collectors.toMap(i -> i.getName(), Function.identity()));
                entityMethodsMap.putAll(superEntityMethodsMap);


                Field[] modelDeclaredFields = model.getClass().getDeclaredFields();
                List<Field> modelFields = new ArrayList<>(Arrays.asList(modelDeclaredFields));
                Field[] superModelDeclaredFields = model.getClass().getSuperclass().getDeclaredFields();
                List<Field> superModelFields = new ArrayList<>(Arrays.asList(superModelDeclaredFields));
                modelFields.addAll(superModelFields);
                for (Field modelField : modelFields) {
                    String modelFieldName = modelField.getName();
                    String typeName = modelField.getType().toString();
                    String modelFieldGetMethodName;
                    if(BL.equals(typeName)) {
                        modelFieldGetMethodName = IS + modelFieldName.substring(0, 1).toUpperCase() + modelFieldName.substring(1);
                    }else {
                        modelFieldGetMethodName = GET + modelFieldName.substring(0, 1).toUpperCase() + modelFieldName.substring(1);
                    }
                    String entityFieldSetMethodName = SET + modelFieldName.substring(0, 1).toUpperCase() + modelFieldName.substring(1);

                    if (entityFieldMap.containsKey(modelFieldName)) {
                        if(modelMethodsMap.containsKey(modelFieldGetMethodName) && entityMethodsMap.containsKey(entityFieldSetMethodName)) {
                            Method modelGetMethod = model.getClass().getMethod(modelFieldGetMethodName);
                            Object modelGetValue = modelGetMethod.invoke(model);
                            String classType = entityFieldMap.get(modelFieldName).toString();
                            classType = classType.replace(CLASS + BLANK, EMPTY);
                            Method entitySetMethod = entity.getClass().getMethod(entityFieldSetMethodName, new Class[]{Class.forName(classType)});
                            entitySetMethod.invoke(entity, new Object[]{modelGetValue});
                        }
                    }
                }
                return entity;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new BaseServiceException(ServiceErrorCodeEnum.MODEL_ENTITY_ERROR);
        }
    }

    public static Object fillModelWithEntity(Object entity) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, BaseServiceException {
        try {
            if (entity != null) {
                String entityClassName = entity.getClass().getSimpleName();
                String modelClassName = entityClassName.substring(0, entityClassName.indexOf(ENTITY)) + MODEL;
                Object model = Class.forName(PATH_MODEL + DOT + modelClassName).newInstance();
                //获取属性
                Field[] modelDeclaredFields = model.getClass().getDeclaredFields();
                List<Field> modelFields = new ArrayList<>(Arrays.asList(modelDeclaredFields));
                //父类属性继承
                Field[] superModelDeclaredFields = model.getClass().getSuperclass().getDeclaredFields();
                List<Field> superModelFields = new ArrayList<>(Arrays.asList(superModelDeclaredFields));
                modelFields.addAll(superModelFields);

                Map<String, Object> modelFieldMap = modelFields.stream().collect(Collectors.toMap(Field::getName, Field::getGenericType));

                Method[] modelMethods = model.getClass().getMethods();
                Map<String, Method> modelMethodsMap = new ArrayList<>(Arrays.asList(modelMethods)).stream().filter(i->(i.getName().startsWith(GET) || i.getName().startsWith(IS) || i.getName().startsWith(SET))).collect(Collectors.toMap(i -> i.getName(), Function.identity()));
                Method[] superModelMethods = model.getClass().getSuperclass().getMethods();
                Map<String, Method> superModelMethodsMap = new ArrayList<>(Arrays.asList(superModelMethods)).stream().filter(i->(i.getName().startsWith(GET) || i.getName().startsWith(IS) || i.getName().startsWith(SET))).collect(Collectors.toMap(i -> i.getName(), Function.identity()));
                modelMethodsMap.putAll(superModelMethodsMap);


                Method[] entityMethods = entity.getClass().getMethods();
                Map<String, Method> entityMethodsMap = new ArrayList<>(Arrays.asList(entityMethods)).stream().filter(i->(i.getName().startsWith(GET) || i.getName().startsWith(IS) || i.getName().startsWith(SET))).collect(Collectors.toMap(i -> i.getName(), Function.identity()));
                Method[] superEntityMethods = entity.getClass().getSuperclass().getMethods();
                Map<String, Method> superEntityMethodsMap = new ArrayList<>(Arrays.asList(superEntityMethods)).stream().filter(i->(i.getName().startsWith(GET) || i.getName().startsWith(IS) || i.getName().startsWith(SET))).collect(Collectors.toMap(i -> i.getName(), Function.identity()));
                entityMethodsMap.putAll(superEntityMethodsMap);

                Field[] entityDeclaredFields = entity.getClass().getDeclaredFields();
                List<Field> entityFields = new ArrayList<>(Arrays.asList(entityDeclaredFields));
                Field[] superEntityDeclaredFields = entity.getClass().getSuperclass().getDeclaredFields();
                List<Field> superEntityFields = new ArrayList<>(Arrays.asList(superEntityDeclaredFields));
                entityFields.addAll(superEntityFields);
                for (Field entityField : entityFields) {
                    String entityFieldName = entityField.getName();
                    String typeName = entityField.getType().toString();
                    String entityFieldGetMethodName;
                    if(BL.equals(typeName)){
                        entityFieldGetMethodName = IS + entityFieldName.substring(0, 1).toUpperCase() + entityFieldName.substring(1);
                    }else {
                        entityFieldGetMethodName = GET + entityFieldName.substring(0, 1).toUpperCase() + entityFieldName.substring(1);
                    }
                    String modelFieldSetMethodName = SET + entityFieldName.substring(0, 1).toUpperCase() + entityFieldName.substring(1);
                    if (modelFieldMap.containsKey(entityFieldName)) {
                        if(modelMethodsMap.containsKey(modelFieldSetMethodName) && entityMethodsMap.containsKey(entityFieldGetMethodName)) {

                            Method entityGetMethod = entity.getClass().getMethod(entityFieldGetMethodName);
                            Object entityGetValue = entityGetMethod.invoke(entity);
                            String classType = modelFieldMap.get(entityFieldName).toString();
                            classType = classType.replace(CLASS + BLANK, EMPTY);
                            Method modelSetMethod = model.getClass().getMethod(modelFieldSetMethodName, new Class[]{Class.forName(classType)});
                            modelSetMethod.invoke(model, new Object[]{entityGetValue});
                        }
                    }
                }
                return model;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new BaseServiceException(ServiceErrorCodeEnum.MODEL_ENTITY_ERROR);
        }
    }

}
