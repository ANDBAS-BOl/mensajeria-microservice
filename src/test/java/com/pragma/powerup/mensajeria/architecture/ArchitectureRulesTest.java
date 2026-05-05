package com.pragma.powerup.mensajeria.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchitectureRulesTest {

    private static final String BASE_PACKAGE = "com.pragma.powerup.mensajeria";
    private static JavaClasses importedClasses;

    @BeforeAll
    static void setUp() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);
    }

    @Test
    void domainShouldNotDependOnInfrastructure() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .check(importedClasses);
    }

    @Test
    void domainShouldNotDependOnApplication() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..application..")
                .check(importedClasses);
    }

    @Test
    void domainShouldNotDependOnSpringFramework() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("org.springframework..")
                .check(importedClasses);
    }

    @Test
    void domainShouldNotDependOnJavaxPersistence() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("javax.persistence..")
                .check(importedClasses);
    }

    @Test
    void applicationHandlersShouldNotDependOnInfrastructureOut() {
        noClasses()
                .that().resideInAPackage("..application.handler..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure.out..")
                .check(importedClasses);
    }

    @Test
    void useCasesShouldOnlyDependOnDomainTypes() {
        classes()
                .that().resideInAPackage("..domain.usecase..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("..domain..", "java..", "lombok..")
                .check(importedClasses);
    }

    @Test
    void controllersShouldNotDependOnDomainUseCases() {
        noClasses()
                .that().resideInAPackage("..infrastructure.input.rest..")
                .should().dependOnClassesThat().resideInAPackage("..domain.usecase..")
                .check(importedClasses);
    }

    @Test
    void noLegacyServicePackageShouldExist() {
        assertTrue(importedClasses.that(inPackage(BASE_PACKAGE + ".service")).isEmpty());
    }

    @Test
    void noLegacyRepositoryPackageAtRoot() {
        assertTrue(importedClasses.that(inPackageStartingWith(BASE_PACKAGE + ".repository")).isEmpty());
    }

    @Test
    void noLegacyClientPackageAtRoot() {
        assertTrue(importedClasses.that(inPackage(BASE_PACKAGE + ".client")).isEmpty());
    }

    @Test
    void noLegacyWebPackageAtRoot() {
        assertTrue(importedClasses.that(inPackageStartingWith(BASE_PACKAGE + ".web")).isEmpty());
    }

    @Test
    void infrastructureSmsAdaptersShouldNotBeReferencedFromDomain() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure.out.sms..")
                .check(importedClasses);
    }

    private static DescribedPredicate<JavaClass> inPackage(String packageName) {
        return new DescribedPredicate<>("reside in package " + packageName) {
            @Override
            public boolean test(JavaClass javaClass) {
                return javaClass.getPackageName().equals(packageName);
            }
        };
    }

    private static DescribedPredicate<JavaClass> inPackageStartingWith(String prefix) {
        return new DescribedPredicate<>("reside in package starting with " + prefix) {
            @Override
            public boolean test(JavaClass javaClass) {
                return javaClass.getPackageName().startsWith(prefix);
            }
        };
    }
}
