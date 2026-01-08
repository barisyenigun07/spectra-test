export type FieldType = "string" | "number" | "boolean" | "select" | "json";

export type FieldCondition = {
  /** Bu field, başka bir field'ın değerine bağlı olarak gösterilecek */
  dependsOn: string; // örn: "platformName"
  /** dependsOn field'ının bu değerlere eşit olması halinde göster */
  equals?: any | any[];
  /** equals yerine: değer bu listede değilse göster */
  notEquals?: any | any[];
};

export type FieldDef = {
  key: string;
  label: string;
  type: FieldType;
  required?: boolean;
  placeholder?: string;
  defaultValue?: any;
  options?: { label: string; value: string }[]; // select için
  help?: string;
  condition?: FieldCondition;
};

export type SchemaDef = {
  title: string;
  fields: FieldDef[];
};