# Challenge Localization API

API REST para localização de pets perdidos a partir de dados de sensores GPS.

## Descrição do Projeto

Esta API recebe dados de sensores acoplados a coleiras de pets (ID do sensor, latitude, longitude, data/hora) e retorna informações detalhadas de localização (país, estado, cidade, bairro, endereço). Utiliza a API externa PositionStack para geocodificação reversa (conversão de coordenadas em endereços).

## Arquitetura

O projeto foi implementado seguindo os princípios de Clean Architecture, com separação clara de responsabilidades em camadas:

### Camadas da Aplicação

1. **Camada de Apresentação** (`presentation`)
   - Controllers REST para receber requisições e retornar respostas
   - DTOs para transferência de dados
   - Manipuladores de exceções para tratamento uniforme de erros

2. **Camada de Aplicação** (`application`)
   - Serviços de aplicação que implementam a lógica de negócio
   - Conversão entre modelos de domínio e DTOs

3. **Camada de Domínio** (`domain`)
   - Modelos de domínio e regras de negócio
   - Interfaces de serviço definindo contratos

4. **Camada de Infraestrutura** (`infrastructure`)
   - Clientes de API externa (PositionStack)
   - Configurações

### Padrões de Design Aplicados

O projeto implementa os seguintes padrões de design:

- **Adapter Pattern**: Integração com a API externa PositionStack através do `PositionStackClient`
- **Builder Pattern**: Construção de objetos complexos nos modelos de domínio
- **Strategy Pattern**: Abstração do serviço de localização através de interfaces

### Princípios SOLID

O projeto segue os princípios SOLID com exemplos concretos:

- **Single Responsibility (SRP)**: 
  - `PetLocationController`: Responsável apenas por receber requisições HTTP
  - `LocationServiceImpl`: Responsável apenas pela lógica de negócio de localização
  - `PositionStackClient`: Responsável apenas pela comunicação com a API externa

- **Open/Closed (OCP)**: 
  - Interface `LocationService` permite adicionar novas implementações (ex: GoogleMaps, MapBox) sem modificar código existente
  - Novos provedores de geocodificação podem ser adicionados implementando a interface

- **Liskov Substitution (LSP)**: 
  - Qualquer implementação de `LocationService` pode substituir `LocationServiceImpl` sem quebrar a funcionalidade
  - `PositionStackClient` pode ser substituído por outros clientes HTTP mantendo o contrato

- **Interface Segregation (ISP)**: 
  - `LocationService` contém apenas o método essencial `getLocationFromCoordinates()`
  - Interfaces específicas e coesas, sem métodos desnecessários para os clientes

- **Dependency Inversion (DIP)**: 
  - `PetLocationController` depende da abstração `LocationService`, não da implementação concreta
  - `LocationServiceImpl` depende da abstração `PositionStackClient`, não de detalhes de implementação
  - Inversão de controle gerenciada pelo Spring Framework

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.5.4
- Spring Cloud OpenFeign (cliente HTTP)
- Spring Validation
- Spring Actuator
- JUnit 5 e Mockito para testes
- JaCoCo para cobertura de testes
- Lombok para redução de boilerplate
- Docker para containerização
- SpringDoc OpenAPI para documentação

## Decisões Técnicas

### Escolha do Spring Boot

O Spring Boot foi escolhido por sua robustez, facilidade de configuração e extensa comunidade. A versão 3.x traz melhorias significativas de desempenho e segurança.

### Uso do OpenFeign

O Spring Cloud OpenFeign simplifica drasticamente a integração com APIs REST externas, transformando chamadas HTTP em interfaces Java simples.

### Arquitetura em Camadas

A separação em camadas permite:
- Melhor testabilidade
- Manutenção mais simples
- Código mais limpo e legível
- Facilidade para estender ou alterar implementações

### Containerização

A disponibilização via Docker facilita a implantação em diferentes ambientes e garante consistência entre desenvolvimento e produção.

### Premissas Assumidas

1. A API PositionStack será a única fonte de dados para geocodificação reversa
2. Não é necessário persistência de dados (armazenamento em banco de dados)
3. A API será usada em um ambiente controlado, sem necessidade de autenticação/autorização
4. Os requisitos de observabilidade podem ser atendidos com logs estruturados e métricas do Spring Actuator

## Como Executar

### ⚠️ **IMPORTANTE - Configuração Obrigatória**

Esta API utiliza o serviço PositionStack para geocodificação. **Você precisa de uma API key para testar a aplicação.**

**1. Obtenha uma API key gratuita (2 minutos):**
   - Acesse: https://positionstack.com/signup/free
   - Cadastre-se com seu email
   - Copie a API key fornecida

**2. Configure a API key:**
```bash
export POSITION_STACK_API_KEY=sua-chave-aqui
```

### 🚀 **Execução Rápida**

Após configurar a API key, escolha uma das opções:

**Opção 1 - Docker Compose (Recomendado):**

```bash
docker-compose up --build -d
```

**Opção 2 - Maven:**
```bash
./mvnw spring-boot:run
```

**Opção 3 - Docker:**
```bash
docker build -t pet-location-api .
docker run -p 8080:8080 -e POSITION_STACK_API_KEY=$POSITION_STACK_API_KEY pet-location-api
```

### 🧪 **Teste da API**

Após iniciar a aplicação, teste com:

```bash
curl -X POST http://localhost:8080/api/v1/locations \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": "PET001",
    "latitude": -23.5505,
    "longitude": -46.6333,
    "timestamp": "2024-01-15T10:30:00Z"
  }'
```

**Resposta esperada:**
```json
{
  "country": "Brazil",
  "state": "São Paulo", 
  "city": "São Paulo",
  "neighborhood": "Jardim Paulista",
  "address": "Avenida Paulista, 2240, São Paulo, Brazil"
}
```

### Pré-requisitos

- Java 17+ (para execução local)
- Docker (recomendado)
- API Key do PositionStack (gratuita)

## Endpoints da API

### Obter Localização de Pet

```
POST /api/v1/locations
```

**Corpo da Requisição:**
```json
{
  "sensorId": "sensor123",
  "latitude": -23.5505,
  "longitude": -46.6333,
  "timestamp": "2023-08-07T14:30:00"
}
```

**Resposta de Sucesso (200 OK):**
```json
{
  "country": "Brazil",
  "state": "São Paulo",
  "city": "São Paulo",
  "neighborhood": "Centro",
  "street": "Avenida Paulista",
  "address": "Avenida Paulista, 123"
}
```

## Testes

O projeto possui uma suíte abrangente de testes que garante a qualidade e confiabilidade da aplicação.

### Cobertura de Testes

- **Cobertura Total**: ~83%
- **Controller**: 100%
- **Service**: 93%
- **Exception Handling**: 54%

### Tipos de Testes Implementados

#### 🧪 **Testes Unitários**

**1. LocationServiceImplTest**
- **Objetivo**: Testa a lógica de negócio do serviço de localização
- **O que testa**:
  - Formatação correta de coordenadas (usando `Locale.US` para decimal com ponto)
  - Mapeamento de dados da API PositionStack para o modelo de domínio
  - Tratamento de fallback para neighborhood (usando `administrativeArea`)
  - Tratamento de valores nulos no campo `label` (endereço)
  - Validação de chamadas para API externa com parâmetros corretos

**2. PetLocationControllerTest**
- **Objetivo**: Testa a camada de apresentação isoladamente
- **O que testa**:
  - Validação de requisições HTTP
  - Serialização/deserialização de DTOs
  - Mapeamento de modelos de domínio para DTOs de resposta
  - Tratamento de respostas HTTP (200 OK)
  - Isolamento da lógica de negócio através de mocks

#### 🔗 **Testes de Integração**

**PetLocationIntegrationTest**
- **Objetivo**: Testa o fluxo completo da aplicação (end-to-end)
- **O que testa**:
  - Integração entre Controller → Service → Client
  - Contexto completo do Spring Boot
  - Serialização JSON real com Jackson
  - Mock da API externa (PositionStack) para testes determinísticos
  - Validação de resposta JSON completa
  - Comportamento da aplicação como um todo

### Estratégias de Teste

#### **Mocking Strategy**
- **Testes Unitários**: Mock de dependências externas para isolamento
- **Testes de Integração**: Mock apenas da API externa (PositionStack)
- **Uso do Mockito**: Para simulação de comportamentos e verificação de interações

#### **Test Data Strategy**
- **Coordenadas Reais**: São Paulo (-23.5505, -46.6333) para testes realistas
- **Dados Determinísticos**: Respostas mockadas consistentes
- **Edge Cases**: Testes para valores nulos e campos opcionais

#### **Assertions Strategy**
- **Validação de Contratos**: Verificação de estrutura de resposta JSON
- **Validação de Dados**: Conferência de valores específicos retornados
- **Validação de Comportamento**: Verificação de chamadas para dependências


## Observabilidade

A API implementa logs detalhados para rastreamento de requisições e possíveis erros. Além disso, fornece endpoints de saúde e métricas através do Spring Actuator:

### Endpoints de Monitoramento

#### Health Check
```bash
# Verificar saúde da aplicação
curl http://localhost:8080/api/actuator/health
```

#### Métricas Personalizadas

A API coleta métricas customizadas para monitoramento em tempo real:

**1. Total de Requisições**
```bash
# Ver total de requisições recebidas
curl http://localhost:8080/api/actuator/metrics/pet.location.requests.total
```

**2. Requisições com Sucesso**
```bash
# Ver total de requisições bem-sucedidas
curl http://localhost:8080/api/actuator/metrics/pet.location.requests.success
```

**3. Requisições com Erro**
```bash
# Ver total de requisições com erro
curl http://localhost:8080/api/actuator/metrics/pet.location.requests.error

# Ver erros por tipo específico
curl "http://localhost:8080/api/actuator/metrics/pet.location.requests.error?tag=error.type:processing_error"
```

**4. Tempo de Resposta**
```bash
# Ver métricas de tempo de resposta (duração das requisições)
curl http://localhost:8080/api/actuator/metrics/pet.location.request.duration
```

**5. Listar Todas as Métricas**
```bash
# Ver todas as métricas disponíveis
curl http://localhost:8080/api/actuator/metrics

# Ver informações detalhadas da aplicação
curl http://localhost:8080/api/actuator/info
```

### Exemplo de Uso Completo

```bash
# 1. Fazer algumas requisições para gerar métricas
curl -X POST http://localhost:8080/api/v1/locations \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": "PET001",
    "latitude": -23.5505,
    "longitude": -46.6333,
    "timestamp": "2024-01-15T10:30:00Z"
  }'

# 2. Verificar métricas coletadas
curl http://localhost:8080/api/actuator/metrics/pet.location.requests.total
curl http://localhost:8080/api/actuator/metrics/pet.location.requests.success
curl http://localhost:8080/api/actuator/metrics/pet.location.request.duration

# 3. Monitorar saúde da aplicação
curl http://localhost:8080/api/actuator/health
```

### Interpretação das Métricas

- **pet.location.requests.total**: Contador total de requisições recebidas
- **pet.location.requests.success**: Contador de requisições processadas com sucesso
- **pet.location.requests.error**: Contador de requisições que falharam (com tags por tipo de erro)
- **pet.location.request.duration**: Timer com estatísticas de tempo de resposta (média, máximo, percentis)

Essas métricas permitem:
- ✅ Monitorar a saúde da API em tempo real
- ✅ Detectar problemas de performance ou falhas
- ✅ Analisar tendências de uso
- ✅ Criar alertas baseados em thresholds
- ✅ Integrar com ferramentas de monitoramento externas

## Possíveis Melhorias Futuras

1. Implementação de cache para requisições frequentes às mesmas coordenadas
2. Circuit breaker para lidar com falhas na API externa
3. Implementação de autenticação/autorização
4. Suporte a múltiplos provedores de geocodificação
5. Persistência de histórico de localizações
6. Implementação de métricas de negócio específicas
