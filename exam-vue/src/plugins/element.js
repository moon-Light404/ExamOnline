import Vue from 'vue'
import {
  Alert,
  Aside,
  Breadcrumb,
  BreadcrumbItem,
  Button,
  Card,
  Checkbox,
  CheckboxGroup,
  Col,
  Container,
  DatePicker,
  Dialog,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  Footer,
  Form,
  FormItem,
  Header,
  Input,
  InputNumber,
  Loading,
  Main,
  Menu,
  MenuItem,
  MenuItemGroup,
  Message,
  MessageBox,
  Notification,
  Option,
  Pagination,
  Radio,
  RadioGroup,
  Row,
  Scrollbar,
  Select,
  Step,
  Steps,
  Submenu,
  Table,
  TableColumn,
  Tag,
  Tooltip,
  Upload
} from 'element-ui'

Vue.prototype.$message = Message
Vue.prototype.$alert = MessageBox.alert
Vue.prototype.$notify = Notification
Vue.prototype.$confirm = MessageBox.confirm
Vue.prototype.$prompt = MessageBox.prompt
Vue.prototype.$Loading = Loading

Vue.use(Button)
Vue.use(Scrollbar)
Vue.use(InputNumber)
Vue.use(Alert)
Vue.use(RadioGroup)
Vue.use(Steps)
Vue.use(Step)
Vue.use(Radio)
Vue.use(DatePicker)
Vue.use(CheckboxGroup)
Vue.use(Checkbox)
Vue.use(Upload)
Vue.use(Dialog)
Vue.use(Loading)
Vue.use(Pagination)
Vue.use(Option)
Vue.use(Select)
Vue.use(Tag)
Vue.use(Table)
Vue.use(TableColumn)
Vue.use(DropdownMenu)
Vue.use(DropdownItem)
Vue.use(Dropdown)
Vue.use(Container)
Vue.use(BreadcrumbItem)
Vue.use(Tooltip)
Vue.use(Main)
Vue.use(Row)
Vue.use(Footer)
Vue.use(Card)
Vue.use(FormItem)
Vue.use(Form)
Vue.use(Input)
Vue.use(Aside)
Vue.use(Menu)
Vue.use(Col)
Vue.use(MenuItem)
Vue.use(MenuItemGroup)
Vue.use(Header)
Vue.use(Submenu)
Vue.use(Breadcrumb)
