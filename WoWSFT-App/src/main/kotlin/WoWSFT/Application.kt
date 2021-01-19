package WoWSFT

import WoWSFT.model.Constant.CUSTOM_SKIlL_GROUP
import WoWSFT.model.Constant.JSON_PARSER
import WoWSFT.model.Constant.TYPE_SHELL
import WoWSFT.model.Constant.TYPE_SHIP
import WoWSFT.model.custom.CustomSkill
import WoWSFT.parser.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.io.File
import java.util.*
import javax.annotation.PostConstruct

@EnableAsync
@SpringBootApplication
class Application : SpringBootServletInitializer()
{
    @PostConstruct
    fun started()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Bean
    fun executor(): ThreadPoolTaskExecutor
    {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5
        executor.setThreadNamePrefix("Thread-")
        executor.initialize()

        return executor
    }

    @Bean(TYPE_SHIP)
    fun zShip(): String
    {
        return ClassPathResource("/json/live/files.zip").file.path
    }

    @Bean(TYPE_SHELL)
    fun zShell(): String
    {
        return ClassPathResource("/json/live/shells.zip").file.path
    }

    @Bean(JSON_PARSER)
    fun jsonParser(): JsonParser
    {
        return JsonParser()
    }

    @Bean(CUSTOM_SKIlL_GROUP)
    fun customSkillGroup(): HashMap<String, List<List<CustomSkill>>> {
        val file = File(ClassPathResource("/json/live/skills.json").file.path)
        return jacksonObjectMapper().readValue(file, jacksonTypeRef())
    }
}

fun main(args: Array<String>)
{
    runApplication<Application>(*args)
}